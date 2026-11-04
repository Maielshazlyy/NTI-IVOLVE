const test = require("node:test");
const assert = require("node:assert/strict");
const checkIvolveDBExists = require("../db-check");

test("returns true when the ivolve database exists", async () => {
  const connection = {
    query: async () => [[{ Database: "ivolve" }], []],
  };

  assert.equal(await checkIvolveDBExists(connection), true);
});

test("returns false when the ivolve database does not exist", async () => {
  const connection = {
    query: async () => [[], []],
  };

  assert.equal(await checkIvolveDBExists(connection), false);
});
