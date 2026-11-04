import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { decode, ok, unavailable, error, pending } from './contract.mjs';

const { cases } = JSON.parse(readFileSync(new URL('../fixtures/answers.json', import.meta.url)));

for (const c of cases) {
  test(c.name, () => assert.deepEqual(decode(c.input), c.decoded));
}

test('builders decode to themselves', () => {
  for (const a of [ok('games', { games: [] }), unavailable('Not built yet.', 'x', 'T'), error('Unreachable.', null, 'unreachable'), pending('x-1', 'x')]) {
    assert.equal(decode(a).status, a.status);
    assert.notEqual(decode(a).code, 'malformed');
  }
});
