import json
import pathlib
import unittest

import ui_contract

CASES = json.loads((pathlib.Path(__file__).parent.parent / "fixtures" / "answers.json").read_text())["cases"]


class Fixtures(unittest.TestCase):
    def test_every_case_decodes_as_written(self):
        for case in CASES:
            with self.subTest(case["name"]):
                self.assertEqual(ui_contract.decode(case["input"]), case["decoded"])

    def test_builders_decode_to_themselves(self):
        answers = [
            ui_contract.ok("games", games=[]),
            ui_contract.unavailable("Not built yet.", slot="x", title="T"),
            ui_contract.error("Unreachable.", code="unreachable"),
            ui_contract.pending("x-1", slot="x"),
        ]
        for answer in answers:
            self.assertEqual(ui_contract.decode(answer)["status"], answer["status"])
            self.assertNotEqual(ui_contract.decode(answer).get("code"), "malformed")

    def test_guarded_turns_a_failure_into_an_error(self):
        seen = []
        answer = ui_contract.guarded("boom", lambda: 1 / 0, log=lambda slot, exc: seen.append(slot))
        self.assertEqual(answer["status"], "error")
        self.assertEqual(answer["slot"], "boom")
        self.assertEqual(seen, ["boom"])


if __name__ == "__main__":
    unittest.main()
