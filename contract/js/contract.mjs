// The backend contract (contract/README.md): every answer is ok, unavailable, error or pending.
// Plain JavaScript with no dependencies, for the HTML test UI.

export const STATUSES = ['ok', 'unavailable', 'error', 'pending'];
export const NO_ANSWER = 'There was no answer.';
export const MALFORMED = 'The answer could not be read.';
const OWN = ['status', 'slot', 'message', 'title', 'code', 'request'];

const malformed = () => ({ status: 'error', message: MALFORMED, code: 'malformed', fields: {} });

/** The one reading of what a backend sent (contract rule 1: anything else is an error). */
export function decode(value) {
  if (value === null || value === undefined) return { status: 'error', message: NO_ANSWER, code: 'no_answer', fields: {} };
  if (Array.isArray(value)) return { status: 'ok', fields: { value } };
  if (typeof value !== 'object' || !STATUSES.includes(value.status)) return malformed();
  for (const name of ['slot', 'message', 'title', 'code', 'request']) {
    if (name in value && typeof value[name] !== 'string') return malformed();
  }
  if ((value.status === 'unavailable' || value.status === 'error') && !value.message) return malformed();
  if (value.status === 'pending' && !value.request) return malformed();
  const decoded = {};
  for (const name of OWN) if (name in value) decoded[name] = value[name];
  decoded.fields = {};
  for (const [k, v] of Object.entries(value)) if (!OWN.includes(k)) decoded.fields[k] = v;
  return decoded;
}

export const ok = (slot, fields = {}) => ({ status: 'ok', ...(slot ? { slot } : {}), ...fields });
export const unavailable = (message, slot, title) => ({ status: 'unavailable', message, ...(slot ? { slot } : {}), ...(title ? { title } : {}) });
export const error = (message, slot, code) => ({ status: 'error', message, ...(slot ? { slot } : {}), ...(code ? { code } : {}) });
export const pending = (request, slot) => ({ status: 'pending', request, ...(slot ? { slot } : {}) });
