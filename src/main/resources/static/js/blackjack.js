const PALOS = {
  Picas:     { simbolo: '♠', color: 'black' },
  Corazones: { simbolo: '♥', color: 'red' },
  Diamantes: { simbolo: '♦', color: 'red' },
  Treboles:  { simbolo: '♣', color: 'black' }
};

const PAUSA_ENTRE_CARTAS = 230;
const DURACION_CARTA = 500;

let saldo = 0;
let apuestaPendiente = 0;
let manoActiva = false;
let cartasJugador = 0;
let ocupado = false;

let dibujadasJugador = [];
let dibujadasDealer = [];

const $ = (id) => document.getElementById(id);
const esperar = (ms) => new Promise(r => setTimeout(r, ms));

function crearCarta(carta) {
  const div = document.createElement('div');
  if (carta === null) {
    div.className = 'card-back';
    div.textContent = '🪙';
    return div;
  }
  const [valor, nombrePalo] = carta.split(' de ');
  const palo = PALOS[nombrePalo];
  div.className = 'card ' + palo.color;
  div.title = carta;
  div.innerHTML = `
      <span>${valor}${palo.simbolo}</span>
      <span class="card-mid">${palo.simbolo}</span>
      <span class="card-bot">${valor}${palo.simbolo}</span>`;
  return div;
}

function mostrarMensaje(texto, tipo = 'info') {
  const el = $('mensaje');
  el.textContent = texto;
  el.className = 'msg-resultado ' + tipo;
}

function mostrarApuesta(monto) {
  $('apuesta-total').textContent = formatoDinero(monto);
  $('quitar-apuesta').textContent = formatoDinero(monto);
}

function actualizarBotones() {
  $('btn-repartir').disabled = ocupado || manoActiva || apuestaPendiente <= 0;
  $('btn-pedir').disabled = ocupado || !manoActiva;
  $('btn-plantarse').disabled = ocupado || !manoActiva;
  $('btn-doblar').disabled = ocupado || !manoActiva || cartasJugador !== 2;
  document.querySelectorAll('.chip').forEach(ch => {
    ch.style.opacity = manoActiva ? '0.4' : '1';
  });
}

const TEXTO_RESULTADO = {
  BLACKJACK: '¡Blackjack!',
  GANASTE: 'Ganaste',
  EMPATE: 'Empate',
  PERDISTE: 'Perdiste',
  TE_PASASTE: 'Te pasaste'
};

function animarReparto(el, retraso) {
  const mazo = $('mazo').getBoundingClientRect();
  const destino = el.getBoundingClientRect();
  const dx = (mazo.left + mazo.width / 2) - (destino.left + destino.width / 2);
  const dy = (mazo.top + mazo.height / 2) - (destino.top + destino.height / 2);
  el.style.setProperty('--dx', dx + 'px');
  el.style.setProperty('--dy', dy + 'px');
  el.style.animationDelay = retraso + 'ms';
  el.classList.add('anim-repartir');

  setTimeout(() => {
    const cuenta = $('mazo-cuenta');
    cuenta.textContent = Math.max(0, Number(cuenta.textContent) - 1);
    $('mazo').classList.remove('saca');
    void $('mazo').offsetWidth;
    $('mazo').classList.add('saca');
  }, retraso);
}

function animarVolteo(dorso, carta, retraso) {
  setTimeout(() => {
    dorso.classList.add('anim-ocultar');
    dorso.addEventListener('animationend', () => {
      const cara = crearCarta(carta);
      cara.classList.add('anim-mostrar');
      dorso.replaceWith(cara);
    }, { once: true });
  }, retraso);
}

async function dibujar(estado, nuevaMano = false) {
  saldo = estado.saldo;
  manoActiva = estado.activa;
  cartasJugador = estado.manoJugador.length;

  if (nuevaMano || estado.manoJugador.length < dibujadasJugador.length) {
    dibujadasJugador = [];
    dibujadasDealer = [];
  }

  const filaJugador = $('cartas-jugador');
  const filaDealer = $('cartas-dealer');
  filaJugador.classList.remove('gana', 'pierde');

  const elsJugador = estado.manoJugador.map(crearCarta);
  const voltear = [];
  const elsDealer = estado.manoDealer.map((carta, i) => {
    const estabaOculta = i < dibujadasDealer.length && dibujadasDealer[i] === null && carta !== null;
    if (estabaOculta) {
      voltear.push(i);
      return crearCarta(null);
    }
    return crearCarta(carta);
  });
  filaJugador.replaceChildren(...elsJugador);
  filaDealer.replaceChildren(...elsDealer);

  const pasos = [];
  const nuevasJ = [...elsJugador.keys()].filter(i => i >= dibujadasJugador.length);
  const nuevasD = [...elsDealer.keys()].filter(i => i >= dibujadasDealer.length);

  if (dibujadasJugador.length === 0 && dibujadasDealer.length === 0) {
    for (let i = 0; i < 2; i++) {
      if (nuevasJ.includes(i)) pasos.push({ tipo: 'repartir', el: elsJugador[i] });
      if (nuevasD.includes(i)) pasos.push({ tipo: 'repartir', el: elsDealer[i] });
    }
    nuevasJ.filter(i => i >= 2).forEach(i => pasos.push({ tipo: 'repartir', el: elsJugador[i] }));
    nuevasD.filter(i => i >= 2).forEach(i => pasos.push({ tipo: 'repartir', el: elsDealer[i] }));
  } else {
    nuevasJ.forEach(i => pasos.push({ tipo: 'repartir', el: elsJugador[i] }));
    voltear.forEach(i => pasos.push({ tipo: 'voltear', el: elsDealer[i], carta: estado.manoDealer[i] }));
    nuevasD.forEach(i => pasos.push({ tipo: 'repartir', el: elsDealer[i] }));
  }

  const repartos = pasos.filter(p => p.tipo === 'repartir').length;
  $('mazo-cuenta').textContent = estado.cartasEnPila + repartos;

  pasos.forEach((paso, k) => {
    const retraso = k * PAUSA_ENTRE_CARTAS;
    if (paso.tipo === 'repartir') animarReparto(paso.el, retraso);
    else animarVolteo(paso.el, paso.carta, retraso);
  });

  dibujadasJugador = [...estado.manoJugador];
  dibujadasDealer = [...estado.manoDealer];

  if (pasos.length > 0) {
    await esperar((pasos.length - 1) * PAUSA_ENTRE_CARTAS + DURACION_CARTA);
  }

  mostrarSaldo(saldo);
  $('puntos-dealer').textContent = estado.puntosDealer + ' / 21';
  $('puntos-jugador').textContent = estado.puntosJugador;
  $('mazo-cuenta').textContent = estado.cartasEnPila;

  if (estado.manoJugador.length > 0) {
    mostrarApuesta(estado.apuesta);
    $('pila-info').textContent =
      `Pila (mazo): ${estado.cartasEnPila} cartas · cada carta repartida es un pop() de la cima`;
  }

  const pill = $('estado-mano');
  if (estado.resultado) {
    pill.hidden = false;
    pill.textContent = TEXTO_RESULTADO[estado.resultado];
    const gano = estado.resultado === 'GANASTE' || estado.resultado === 'BLACKJACK';
    if (gano) filaJugador.classList.add('gana');
    if (estado.resultado === 'PERDISTE' || estado.resultado === 'TE_PASASTE') filaJugador.classList.add('pierde');

    const extra = estado.pago > 0 ? ` (+${formatoDinero(estado.pago)})` : '';

    if (apuestaPendiente > saldo) apuestaPendiente = 0;
    const siguiente = apuestaPendiente > 0
      ? ` · Repartir repite ${formatoDinero(apuestaPendiente)}`
      : ' · Elige fichas para jugar otra vez';
    mostrarMensaje(estado.mensaje + extra + siguiente,
      gano || estado.resultado === 'EMPATE' ? 'info' : 'warn');
  } else if (manoActiva) {
    pill.hidden = true;
    mostrarMensaje('Tu turno: pide carta, plántate o dobla');
  } else {
    pill.hidden = true;
  }
}

async function accion(url, datos) {
  ocupado = true;
  actualizarBotones();
  try {
    const estado = await api(url, datos);
    await dibujar(estado, url.endsWith('/iniciar'));
  } catch (err) {
    mostrarMensaje(err.message, 'warn');
  } finally {
    ocupado = false;
    actualizarBotones();
  }
}

document.querySelectorAll('.chip').forEach(ficha => {
  ficha.addEventListener('click', () => {
    if (manoActiva || ocupado) return;
    const valor = Number(ficha.dataset.valor);
    if (apuestaPendiente + valor > saldo) {
      mostrarMensaje('No te alcanza el saldo para esa ficha', 'warn');
      return;
    }
    apuestaPendiente += valor;
    document.querySelectorAll('.chip').forEach(c => c.classList.remove('activa'));
    ficha.classList.add('activa');
    mostrarApuesta(apuestaPendiente);
    mostrarMensaje(`Apuesta: ${formatoDinero(apuestaPendiente)} · presiona Repartir`);
    actualizarBotones();
  });
});

$('quitar-apuesta').addEventListener('click', () => {
  if (manoActiva || ocupado) return;
  apuestaPendiente = 0;
  document.querySelectorAll('.chip').forEach(c => c.classList.remove('activa'));
  mostrarApuesta(0);
  mostrarMensaje('Toca las fichas para apostar y presiona Repartir');
  actualizarBotones();
});

$('btn-repartir').addEventListener('click', () => accion('/api/blackjack/iniciar', { monto: apuestaPendiente }));
$('btn-pedir').addEventListener('click', () => accion('/api/blackjack/pedir', {}));
$('btn-plantarse').addEventListener('click', () => accion('/api/blackjack/plantarse', {}));
$('btn-doblar').addEventListener('click', () => accion('/api/blackjack/doblar', {}));

(async () => {
  ocupado = true;
  actualizarBotones();
  try {
    const estado = await api('/api/blackjack/estado');
    await dibujar(estado, true);
  } catch (err) {
    mostrarMensaje(err.message, 'warn');
  }
  ocupado = false;
  actualizarBotones();
})();
