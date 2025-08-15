/* ============================
   Config geral da API
   ============================ */
// Se sua app tiver context-path (ex.: /blue), deixe vazio mesmo e o navegador resolve relativo.
// Se a API for em outro host/porta, troque para "http://localhost:8081" etc.
const API_BASE = "";

// Endpoints esperados no backend (ajuste os nomes se forem diferentes)
const ENDPOINT_METRICS = "/api/admin/metrics";
const ENDPOINT_ULTIMAS_VENDAS = "/api/admin/ultimas-vendas";

/* ============================
   Utilidades
   ============================ */
const sidebar = document.getElementById('sidebar');
const toggleBtn = document.getElementById('toggleSidebar');
toggleBtn.addEventListener('click', () => sidebar.classList.toggle('collapsed'));

const userNameEl = document.getElementById('userName');
const token = localStorage.getItem('token');                 // "Bearer eyJ..."
const userName = localStorage.getItem('userName') || 'Usuário';
userNameEl.textContent = userName;

function toBRL(n) {
  const v = typeof n === "number" ? n : Number(n || 0);
  return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}
function pct(delta) {
  const d = Number(delta || 0);
  const sign = d > 0 ? '+' : '';
  return sign + d.toFixed(1).replace('.', ',') + '%';
}
function requireAuthOrRedirect(resp) {
  if (resp.status === 401 || resp.status === 403) {
    // Sem autorização: manda pra login
    window.location.href = '/login';
    throw new Error('Não autorizado');
  }
}

/* ============================
   Fetch helpers
   ============================ */
async function apiGet(path) {
  const resp = await fetch(API_BASE + path, {
    headers: {
      'Accept': 'application/json',
      ...(token ? { 'Authorization': token } : {})
    },
    credentials: 'same-origin'
  });
  requireAuthOrRedirect(resp);
  if (!resp.ok) {
    const txt = await resp.text().catch(() => '');
    throw new Error(`Falha ao buscar ${path}: ${resp.status} ${txt}`);
  }
  return resp.json();
}

/* ============================
   KPIs (API)
   Espera um JSON assim (exemplo):
   {
     "usuariosAtivos": 213,
     "pedidos24h": 71,
     "faturamento": 15342.75,
     "ticketMedio": 215.41,
     "deltas": { "usuariosAtivos": 3.2, "pedidos24h": -1.1, "faturamento": 6.5, "ticketMedio": 2.1 },
     "serie": [ { "t": "2025-08-10", "v": 120 }, ... ]  // opcional p/ gráfico
   }
   ============================ */
async function loadKpisFromApi() {
  const data = await apiGet(ENDPOINT_METRICS);

  // Valores principais
  document.getElementById('kpiUsers').textContent    = data.usuariosAtivos ?? '--';
  document.getElementById('kpiOrders').textContent   = data.pedidos24h ?? '--';
  document.getElementById('kpiRevenue').textContent  = toBRL(data.faturamento ?? 0);
  document.getElementById('kpiAvg').textContent      = toBRL(data.ticketMedio ?? 0);

  // Deltas
  const d = data.deltas || {};
  setDelta('kpiUsersDelta', d.usuariosAtivos);
  setDelta('kpiOrdersDelta', d.pedidos24h);
  setDelta('kpiRevenueDelta', d.faturamento);
  setDelta('kpiAvgDelta', d.ticketMedio);

  // Série opcional para o “gráfico”
  if (Array.isArray(data.serie) && data.serie.length > 1) {
    renderChartWithSeries(data.serie.map(p => Number(p.v)));
  } else {
    renderChart(); // fallback se não vier série
  }
}

function setDelta(elId, value) {
  const el = document.getElementById(elId);
  const num = Number(value || 0);
  el.textContent = pct(num);
  el.className = 'kpi-delta ' + (num >= 0 ? 'up' : 'down');
}

/* ============================
   Tabela (API)
   Espera um JSON array assim:
   [
     { "id": 101, "cliente": "Ana", "produto": "Notebook", "valor": 3499.90, "data": "2025-08-13T09:10:00" },
     ...
   ]
   ============================ */
async function loadTableFromApi() {
  const list = await apiGet(ENDPOINT_ULTIMAS_VENDAS);
  renderTable(list);
}

function renderTable(rows) {
  const tbody = document.getElementById('ordersTable');
  if (!Array.isArray(rows) || rows.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5">Sem dados recentes</td></tr>`;
    return;
  }
  tbody.innerHTML = rows.map((r, i) => {
    const dt = r.data ? new Date(r.data) : new Date();
    return `
      <tr>
        <td>${String(r.id ?? i + 1).toString().padStart(2, '0')}</td>
        <td>${r.cliente ?? '-'}</td>
        <td>${r.produto ?? '-'}</td>
        <td>${toBRL(r.valor ?? 0)}</td>
        <td>${dt.toLocaleString('pt-BR')}</td>
      </tr>
    `;
  }).join('');
}

/* ============================
   Gráfico (sem libs)
   ============================ */
function renderChart() {
  // mantém a versão random se a API não mandar série
  const el = document.getElementById('chartArea');
  const w = el.clientWidth, h = el.clientHeight;
  const points = Array.from({ length: 20 }, () => Math.random());
  renderChartSVG(el, w, h, points);
}

function renderChartWithSeries(seriesValues) {
  const el = document.getElementById('chartArea');
  const w = el.clientWidth, h = el.clientHeight;
  // normaliza a série para 0..1
  const max = Math.max(...seriesValues.map(v => Number(v) || 0), 1);
  const min = Math.min(...seriesValues.map(v => Number(v) || 0), 0);
  const range = Math.max(max - min, 1e-6);
  const points = seriesValues.map(v => (Number(v) - min) / range);
  renderChartSVG(el, w, h, points);
}

function renderChartSVG(el, w, h, points) {
  const path = points.map((p, i) => {
    const x = (i / (points.length - 1)) * (w - 24) + 12;
    const y = (1 - p) * (h - 24) + 12;
    return `${i === 0 ? 'M' : 'L'} ${x.toFixed(1)} ${y.toFixed(1)}`;
  }).join(' ');
  const circles = points.map((p, i) => {
    const x = (i / (points.length - 1)) * (w - 24) + 12;
    const y = (1 - p) * (h - 24) + 12;
    return `<circle cx="${x.toFixed(1)}" cy="${y.toFixed(1)}" r="3" fill="#60a5fa" />`;
  }).join('');
  el.innerHTML = `
    <svg width="${w}" height="${h}" viewBox="0 0 ${w} ${h}" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Série temporal">
      <defs>
        <linearGradient id="g1" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stop-color="#60a5fa" stop-opacity="0.35"/>
          <stop offset="100%" stop-color="#60a5fa" stop-opacity="0"/>
        </linearGradient>
      </defs>
      <path d="${path}" fill="none" stroke="#93c5fd" stroke-width="2"/>
      <path d="${path} L ${w-12} ${h-12} L 12 ${h-12} Z" fill="url(#g1)" />
      ${circles}
    </svg>
  `;
}

/* ============================
   Tabela (mock fallback)
   ============================ */
const sampleNames = ['Ana', 'Bruno', 'Carla', 'Diego', 'Edu', 'Fernanda', 'Gabi', 'Hugo', 'Igor', 'Julia'];
const sampleProducts = ['Arroz 5kg', 'Feijão Preto 1kg', 'Macarrão Espaguete 500g', 'Açúcar 1kg',
                          'Óleo de Soja 900ml', 'Leite Integral 1L', 'Pão Francês 1kg', 'Manteiga 200g',
                          'Café Torrado 500g', 'Refrigerante 2L', 'Suco de Laranja 1L', 'Água Mineral 1,5L',
                          'Frango Inteiro 1kg', 'Carne Bovina Patinho 1kg', 'Linguiça Toscana 1kg',
                          'Tomate 1kg', 'Banana Nanica 1kg', 'Maçã Gala 1kg', 'Alface Crespa Unidade',
                          'Detergente Líquido 500ml', 'Sabão em Pó 1kg', 'Papel Higiênico 12 rolos',
                          'Shampoo 350ml', 'Sabonete 90g'];

function loadTableMock() {
  const rows = Array.from({ length: 8 }, (_, i) => {
    const name = sampleNames[Math.floor(Math.random() * sampleNames.length)];
    const prod = sampleProducts[Math.floor(Math.random() * sampleProducts.length)];
    const value = (Math.random() * 1200 + 49.9);
    const date = new Date(Date.now() - Math.random() * 86400000 * 3);
    return { id: i + 1, cliente: name, produto: prod, valor: value, data: date.toISOString() };
  });
  renderTable(rows);
}

/* ============================
   Eventos UI
   ============================ */
document.getElementById('logoutBtn').addEventListener('click', () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userName');
  window.location.href = '/login';
});

document.getElementById('refreshTable').addEventListener('click', async () => {
  try {
    await loadTableFromApi();
  } catch {
    loadTableMock();
  }
});

document.getElementById('year').textContent = new Date().getFullYear();

/* ============================
   Inicialização
   ============================ */
async function init() {
  // Sem token? se preferir, barra pelo front também:
  // if (!token) return (window.location.href = '/login');

  try {
    await loadKpisFromApi();
  } catch (e) {
    // fallback visual (mock) se API estiver off
    console.warn('KPIs: usando mock', e);
    loadKpisMock();
    renderChart();
  }

  try {
    await loadTableFromApi();
  } catch (e) {
    console.warn('Tabela: usando mock', e);
    loadTableMock();
  }

  window.addEventListener('resize', renderChart);
}
init();

/* ===== Mock de KPIs (fallback) ===== */
function loadKpisMock() {
  const users = Math.floor(Math.random() * 240 + 80);
  const orders = Math.floor(Math.random() * 100 + 20);
  const revenue = Math.random() * 15000 + 2000;
  const avg = revenue / Math.max(orders, 1);

  document.getElementById('kpiUsers').textContent = users;
  document.getElementById('kpiOrders').textContent = orders;
  document.getElementById('kpiRevenue').textContent = toBRL(revenue);
  document.getElementById('kpiAvg').textContent = toBRL(avg);

  setDelta('kpiUsersDelta', Math.random() * 8 - 1);
  setDelta('kpiOrdersDelta', Math.random() * 8 - 4);
  setDelta('kpiRevenueDelta', Math.random() * 10);
  setDelta('kpiAvgDelta', Math.random() * 6);
}
