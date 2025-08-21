// Troca Login <-> Cadastro


// Mostrar/Ocultar senha
document.querySelectorAll('[data-toggle-password]').forEach(btn => {
btn.addEventListener('click', () => {
const id = btn.getAttribute('data-toggle-password');
const input = document.getElementById(id);
if(!input) return;
input.type = input.type === 'password' ? 'text' : 'password';
});
});


function setFieldError(input, message){
const p = document.querySelector(`[data-error-for="${input.id}"]`);
if(!p) return;
if(message){ p.textContent = message; p.classList.remove('hidden'); }
else { p.textContent = ''; p.classList.add('hidden'); }
}


function isValidEmail(email){ return /.+@.+\..+/.test(email); }


// Login
const loginForm = document.getElementById('loginForm');
loginForm.addEventListener('submit', async (e)=>{
e.preventDefault();
const email = document.getElementById('loginEmail');
const pass = document.getElementById('loginPassword');


setFieldError(email, '');
setFieldError(pass, '');


let ok = true;
if(!isValidEmail(email.value)) { setFieldError(email, 'Informe um email válido.'); ok=false; }
if(pass.value.length < 6) { setFieldError(pass, 'A senha deve ter pelo menos 6 caracteres.'); ok=false; }
if(!ok) return;


try{
// Substitua pela sua API real
// const resp = await fetch('/api/auth/login', {
// method:'POST', headers:{'Content-Type':'application/json'},
// body: JSON.stringify({ email: email.value, senha: pass.value })
// });
// const data = await resp.json();
// if(!resp.ok) throw new Error(data.message || 'Falha no login');
alert('Login enviado (troque pelo fetch do backend).');
// location.href = '/dashboard.html';
}catch(err){ alert('Erro: ' + err.message); }
});


// Cadastro
const signupForm = document.getElementById('signupForm');
signupForm.addEventListener('submit', async (e)=>{
e.preventDefault();
const email = document.getElementById('signupEmail');
const pass = document.getElementById('signupPassword');
const conf = document.getElementById('signupConfirm');


setFieldError(email, '');
setFieldError(pass, '');
setFieldError(conf, '');


let ok = true;
if(!isValidEmail(email.value)) { setFieldError(email, 'Informe um email válido.'); ok=false; }
if(pass.value.length < 6) { setFieldError(pass, 'Mínimo de 6 caracteres.'); ok=false; }
if(conf.value !== pass.value) { setFieldError(conf, 'As senhas não conferem.'); ok=false; }
if(!ok) return;


try{
// Substitua pela sua API real
// const resp = await fetch('/api/auth/signup', {
// method:'POST', headers:{'Content-Type':'application/json'},
// body: JSON.stringify({ email: email.value, senha: pass.value })
// });
// const data = await resp.json();
// if(!resp.ok) throw new Error(data.message || 'Falha no cadastro');
alert('Cadastro enviado (troque pelo fetch do backend).');
}catch(err){ alert('Erro: ' + err.message); }
});