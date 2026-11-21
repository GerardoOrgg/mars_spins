const ORDEN_RUEDA = [0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10,
                     5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26];
const GRADOS_CASILLA = 360 / 37;
const DURACION_GIRO = 4200;
const COLORES = { rojo: '#c0392b', negro: '#1a1a2e', verde: '#059669' };

let saldo = 0;
let color = 'rojo';
let apuesta = 0;
let girando = false;
let casillas = [];
let giroRueda = 0;
let giroPista = 0;

const $ = (id) => document.getElementById(id);
const esperar = (ms) => new Promise(r => setTimeout(r, ms));

function partes(casilla) {
  const [numero, nombreColor] = casilla.split(' - ');
  return { numero, color: nombreColor.toLowerCase() };
}

function mostrarMensaje(texto, tipo = 'info') {
  $('mensaje').textContent = texto;
  $('mensaje').className = 'msg-resultado ' + tipo;
}

function actualizar() {
  $('apuesta').textContent = formatoDinero(apuesta);
  $('btn-girar').disabled = girando || apuesta <= 0;
}

function punto(radio, grados) {
  const rad = grados * Math.PI / 180;
  return [radio * Math.sin(rad), -radio * Math.cos(rad)];
}

function dibujarRueda() {
  const R_EXT = 96, R_INT = 66;
  let casillasSvg = '';

  ORDEN_RUEDA.forEach((numero, pos) => {
    const centro = pos * GRADOS_CASILLA;
    const a0 = centro - GRADOS_CASILLA / 2;
    const a1 = centro + GRADOS_CASILLA / 2;
    const [x0, y0] = punto(R_EXT, a0), [x1, y1] = punto(R_EXT, a1);
    const [x2, y2] = punto(R_INT, a1), [x3, y3] = punto(R_INT, a0);
    const relleno = COLORES[partes(casillas[numero]).color];
    casillasSvg += `<path d="M${x0},${y0} A${R_EXT},${R_EXT} 0 0 1 ${x1},${y1} L${x2},${y2}
                     A${R_INT},${R_INT} 0 0 0 ${x3},${y3}Z" fill="${relleno}" stroke="#d9a62c" stroke-width=".6"/>`;
    const [tx, ty] = punto(87, centro);
    casillasSvg += `<text x="${tx}" y="${ty}" transform="rotate(${centro} ${tx} ${ty})" fill="#fff"
                     font-size="8" font-weight="700" font-family="Arial" text-anchor="middle"
                     dominant-baseline="middle">${numero}</text>`;
  });

  const rayos = [0, 90, 180, 270].map(g => {
    const [x, y] = punto(62, g);
    return `<line x1="0" y1="0" x2="${x}" y2="${y}" stroke="url(#oroRueda)" stroke-width="3.5" stroke-linecap="round"/>`;
  }).join('');

  $('rueda').innerHTML = `
    <defs>
      <radialGradient id="maderaRueda" cx="50%" cy="40%" r="65%">
        <stop offset="0" stop-color="#6b3a1f"/><stop offset="1" stop-color="#2b1408"/>
      </radialGradient>
      <linearGradient id="oroRueda" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0" stop-color="#ffe08a"/><stop offset=".5" stop-color="#f5be38"/><stop offset="1" stop-color="#a8740f"/>
      </linearGradient>
    </defs>
    <circle r="108" fill="url(#maderaRueda)" stroke="url(#oroRueda)" stroke-width="4"/>
    <circle r="98" fill="#120d22"/>
    ${casillasSvg}
    <circle r="66" fill="url(#maderaRueda)" stroke="url(#oroRueda)" stroke-width="2"/>
    ${rayos}`;
}

async function animarGiro(numeroGanador) {
  const posicion = ORDEN_RUEDA.indexOf(numeroGanador);

  const objetivo = -posicion * GRADOS_CASILLA;
  const faltante = (((objetivo - giroRueda) % 360) + 360) % 360;
  giroRueda += 4 * 360 + faltante;

  giroPista -= 6 * 360 + (((giroPista % 360) + 360) % 360);

  const rueda = $('rueda'), pista = $('pista'), bolita = $('bolita');
  bolita.classList.remove('cae');
  rueda.style.transition = `transform ${DURACION_GIRO}ms cubic-bezier(.12,.6,.1,1)`;
  pista.style.transition = `transform ${DURACION_GIRO}ms cubic-bezier(.2,.65,.15,1)`;
  rueda.style.transform = `rotate(${giroRueda}deg)`;
  pista.style.transform = `rotate(${giroPista}deg)`;

  await esperar(DURACION_GIRO * 0.72);
  bolita.classList.add('cae');
  await esperar(DURACION_GIRO * 0.28 + 150);
}

function marcarCelda(indice) {
  document.querySelectorAll('.celda.marcada').forEach(el => el.classList.remove('marcada'));
  const celda = $('celda-' + indice);
  if (celda) celda.classList.add('marcada');
}

function mostrarNumero(indice) {
  const p = partes(casillas[indice]);
  const bola = $('bola');
  bola.textContent = p.numero;
  bola.className = 'bola ' + p.color;
  void bola.offsetWidth;
  bola.classList.add('revelar');
}

function agregarHistorial(indice) {
  const p = partes(casillas[indice]);
  const bolita = document.createElement('span');
  bolita.className = p.color;
  bolita.textContent = p.numero;
  $('historial').prepend(bolita);
  while ($('historial').children.length > 8) {
    $('historial').lastChild.remove();
  }
}

function pintarArreglo() {
  $('arreglo').innerHTML = casillas.map((c, i) => {
    const p = partes(c);
    return `<div class="celda ${p.color}" id="celda-${i}"><small>[${i}]</small>${p.numero}</div>`;
  }).join('');
}

document.querySelectorAll('.btn-color').forEach(btn => {
  btn.addEventListener('click', () => {
    if (girando) return;
    color = btn.dataset.color;
    document.querySelectorAll('.btn-color').forEach(b => b.classList.toggle('activo', b === btn));
  });
});

document.querySelectorAll('.chip').forEach(ficha => {
  ficha.addEventListener('click', () => {
    if (girando) return;
    const valor = Number(ficha.dataset.valor);
    if (apuesta + valor > saldo) {
      mostrarMensaje('No te alcanza el saldo para esa ficha', 'warn');
      return;
    }
    apuesta += valor;
    document.querySelectorAll('.chip').forEach(c => c.classList.remove('activa'));
    ficha.classList.add('activa');
    mostrarMensaje(`Apuesta ${formatoDinero(apuesta)} al ${color}`);
    actualizar();
  });
});

$('btn-limpiar').addEventListener('click', () => {
  if (girando) return;
  apuesta = 0;
  document.querySelectorAll('.chip').forEach(c => c.classList.remove('activa'));
  actualizar();
});

$('btn-girar').addEventListener('click', async () => {
  girando = true;
  actualizar();
  mostrarMensaje('No va más... ¡girando!');
  $('bola').textContent = '?';
  $('bola').className = 'bola espera';

  let resultado;
  try {
    resultado = await api('/api/ruleta/girar', { color, monto: apuesta });
  } catch (err) {
    girando = false;
    actualizar();
    mostrarMensaje(err.message, 'warn');
    return;
  }

  await animarGiro(resultado.indice);

  girando = false;
  mostrarNumero(resultado.indice);
  marcarCelda(resultado.indice);
  agregarHistorial(resultado.indice);
  saldo = resultado.saldo;
  mostrarSaldo(saldo);

  const salio = `Salió ${resultado.casilla} (ruleta[${resultado.indice}])`;
  if (resultado.gano) {
    mostrarMensaje(`${salio} · ¡Ganaste ${formatoDinero(resultado.ganancia)}!`);
  } else {
    mostrarMensaje(`${salio} · Perdiste ${formatoDinero(resultado.apuesta)}`, 'warn');
  }
  if (apuesta > saldo) apuesta = 0;
  actualizar();
});

(async () => {
  try {
    const [cuenta, arreglo] = await Promise.all([cargarCuenta(), api('/api/ruleta/casillas')]);
    saldo = cuenta.saldo;
    casillas = arreglo;
    dibujarRueda();
    pintarArreglo();
  } catch (err) {
    mostrarMensaje(err.message, 'warn');
  }
  actualizar();
})();
