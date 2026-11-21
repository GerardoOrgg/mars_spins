async function api(url, datos) {
  const opciones = { method: datos === undefined ? 'GET' : 'POST', headers: {} };
  if (datos !== undefined) {
    opciones.headers['Content-Type'] = 'application/json';
    opciones.body = JSON.stringify(datos);
  }

  let respuesta;
  try {
    respuesta = await fetch(url, opciones);
  } catch (e) {
    throw new Error('No hay conexion con el servidor. ¿Esta corriendo Spring Boot?');
  }

  if (respuesta.status === 401) {
    window.location.href = 'login.html';
    throw new Error('Inicia sesion para continuar');
  }

  const cuerpo = respuesta.status === 204 ? null : await respuesta.json().catch(() => null);
  if (!respuesta.ok) {
    throw new Error((cuerpo && cuerpo.error) || 'Ocurrio un error en el servidor');
  }
  return cuerpo;
}

function formatoDinero(cantidad) {
  const decimales = Number.isInteger(cantidad) ? 0 : 2;
  return '$' + cantidad.toLocaleString('es-MX', {
    minimumFractionDigits: decimales,
    maximumFractionDigits: 2
  });
}

function mostrarSaldo(saldo) {
  document.querySelectorAll('.balance-val').forEach(el => {
    el.textContent = formatoDinero(saldo);
  });
}

async function cargarCuenta() {
  const cuenta = await api('/api/cuenta');
  mostrarSaldo(cuenta.saldo);
  document.querySelectorAll('[data-usuario]').forEach(el => {
    el.textContent = cuenta.nombre;
  });
  return cuenta;
}

async function cerrarSesion() {
  await api('/api/logout', {});
  window.location.href = 'login.html';
}
