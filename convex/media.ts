import { mutation } from "./_generated/server";
import { v } from "convex/values";

export const generateUploadUrl = mutation({
  args: {},
  handler: async (ctx) => {
    const identity = await ctx.auth.getUserIdentity();
    if (!identity) throw new Error("UNAUTHENTICATED");
    return ctx.storage.generateUploadUrl();
  },
});

export const complete = mutation({
  args: {
    localId: v.string(),
    noteLocalId: v.string(),
    storageId: v.id("_storage"),
    kind: v.union(v.literal("image"), v.literal("ink")),
    mime: v.string(),
    width: v.optional(v.number()),
    height: v.optional(v.number()),
    updatedAt: v.number(),
  },
  handler: async (ctx, args) => {
    const identity = await ctx.auth.getUserIdentity();
    if (!identity) throw new Error("UNAUTHENTICATED");
    await ctx.db.insert("media", { clerkUserId: identity.subject, ...args });
  },
});
