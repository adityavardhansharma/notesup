import { defineSchema, defineTable } from "convex/server";
import { v } from "convex/values";

export default defineSchema({
  users: defineTable({
    clerkUserId: v.string(),
    createdAt: v.number(),
  }).index("by_clerk", ["clerkUserId"]),

  projects: defineTable({
    clerkUserId: v.string(),
    localId: v.string(),
    name: v.string(),
    hue: v.number(),
    emoji: v.optional(v.string()),
    order: v.number(),
    updatedAt: v.number(),
    deletedAt: v.optional(v.number()),
  })
    .index("by_user", ["clerkUserId", "order"])
    .index("by_user_local", ["clerkUserId", "localId"]),

  notes: defineTable({
    clerkUserId: v.string(),
    localId: v.string(),
    projectLocalId: v.optional(v.string()),
    title: v.string(),
    blocks: v.any(),
    pinned: v.boolean(),
    locked: v.boolean(),
    ciphertext: v.optional(v.string()),
    tint: v.number(),
    paper: v.optional(v.string()),
    font: v.optional(v.string()),
    updatedAt: v.number(),
    createdAt: v.number(),
    deletedAt: v.optional(v.number()),
    rev: v.number(),
    writerId: v.string(),
    baseRev: v.number(),
    baseWriterId: v.optional(v.string()),
  })
    .index("by_user_updated", ["clerkUserId", "updatedAt"])
    .index("by_user_local", ["clerkUserId", "localId"])
    .index("by_user_pinned", ["clerkUserId", "pinned", "updatedAt"])
    .index("by_user_project", ["clerkUserId", "projectLocalId", "updatedAt"]),

  media: defineTable({
    clerkUserId: v.string(),
    localId: v.string(),
    noteLocalId: v.string(),
    storageId: v.id("_storage"),
    kind: v.union(v.literal("image"), v.literal("ink")),
    mime: v.string(),
    width: v.optional(v.number()),
    height: v.optional(v.number()),
    updatedAt: v.number(),
  })
    .index("by_user_local", ["clerkUserId", "localId"])
    .index("by_note", ["clerkUserId", "noteLocalId"]),
});
