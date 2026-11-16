const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const DATABASE_ID = "ai-studio-android-33293474-fe6f-4740-9535-e2b30edf2579";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function pastTimestamp() {
  return new Date(Date.now() - 1000);
}

function dbFor(context) {
  return context.firestore({ databaseId: DATABASE_ID });
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write profiles", async () => {
  const unauthDb = dbFor(testEnv.unauthenticatedContext());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      childName: "نور",
      avatarId: "owl",
      totalStars: 0,
      streakDays: 1,
      completedQuizzes: 0,
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    })
  );
});

test("Authenticated owner can create and update their ChildProfile", async () => {
  const aliceDb = dbFor(testEnv.authenticatedContext(ALICE_UID));
  const docRef = aliceDb.collection("users").doc(ALICE_UID);

  await assertSucceeds(
    docRef.set({
      userId: ALICE_UID,
      childName: "ياسين",
      avatarId: "owl",
      totalStars: 0,
      streakDays: 1,
      completedQuizzes: 0,
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    })
  );

  await assertSucceeds(
    docRef.update({
      totalStars: 15,
      completedQuizzes: 1,
      updatedAt: pastTimestamp(),
    })
  );

  await assertSucceeds(docRef.get());
});

test("Rejects profile creation with ghost field or wrong userId", async () => {
  const aliceDb = dbFor(testEnv.authenticatedContext(ALICE_UID));
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      childName: "ياسين",
      avatarId: "owl",
      totalStars: 0,
      streakDays: 1,
      completedQuizzes: 0,
      isAdmin: true,
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      childName: "سارة",
      avatarId: "cat",
      totalStars: 0,
      streakDays: 1,
      completedQuizzes: 0,
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    })
  );
});

test("Cross-user access to profile, masteredWords, and quizSessions is denied", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const adminDb = dbFor(ctx);
    await adminDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      childName: "سارة",
      avatarId: "cat",
      totalStars: 20,
      streakDays: 2,
      completedQuizzes: 1,
      createdAt: pastTimestamp(),
      updatedAt: pastTimestamp(),
    });
    await adminDb
      .collection("users")
      .doc(BOB_UID)
      .collection("masteredWords")
      .doc("alpha_a")
      .set({
        userId: BOB_UID,
        wordId: "alpha_a",
        categoryId: "alphabet",
        englishWord: "Apple",
        starsEarned: 5,
        masteredAt: pastTimestamp(),
      });
  });

  const aliceDb = dbFor(testEnv.authenticatedContext(ALICE_UID));
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("masteredWords")
      .get()
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("quizSessions")
      .get()
  );
});

test("Authenticated owner can write/read masteredWords and append quizSessions, but cannot update/delete quizSessions", async () => {
  const aliceDb = dbFor(testEnv.authenticatedContext(ALICE_UID));
  const wordRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("masteredWords")
    .doc("alpha_a");

  await assertSucceeds(
    wordRef.set({
      userId: ALICE_UID,
      wordId: "alpha_a",
      categoryId: "alphabet",
      englishWord: "Apple",
      starsEarned: 5,
      masteredAt: pastTimestamp(),
    })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("masteredWords")
      .get()
  );

  const sessionRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("quizSessions")
    .doc("session_1");

  await assertSucceeds(
    sessionRef.set({
      userId: ALICE_UID,
      categoryId: "alphabet",
      gameType: "listen_quiz",
      score: 4,
      totalQuestions: 5,
      starsEarned: 40,
      createdAt: pastTimestamp(),
    })
  );

  await assertFails(sessionRef.update({ score: 5 }));
  await assertFails(sessionRef.delete());
});
