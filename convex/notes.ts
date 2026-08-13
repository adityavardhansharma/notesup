import { mutation, query } from "./_generated/server";
import { v } from "convex/values";

async function uid(ctx: any) {
  const identity = await ctx.auth.getUserIdentity();
  if (!identity) throw new Error("UNAUTHENTICATED");
  return identity.subject as string;
}

export const list = query({
  args: { since: v.optional(v.number()) },
  handler: async (ctx, args) => {
    const clerkUserId = await uid(ctx);
    return ctx.db
      .query("notes")
      .withIndex("by_user_updated", (q) => q.eq("clerkUserId", clerkUserId))
      .order("desc")
      .take(200);
  },
});

export const get = query({
  args: { localId: v.string() },
  handler: async (ctx, args) => {
    const clerkUserId = await uid(ctx);
    return ctx.db
      .query("notes")
      .withIndex("by_user_local", (q) => q.eq("clerkUserId", clerkUserId).eq("localId", args.localId))
      .unique();
  },
});

export const upsert = mutation({
  args: {
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
  },
  handler: async (ctx, args) => {
    const clerkUserId = await uid(ctx);
    const existing = await ctx.db
      .query("notes")
      .withIndex("by_user_local", (q) => q.eq("clerkUserId", clerkUserId).eq("localId", args.localId))
      .unique();
    if (!existing) {
      await ctx.db.insert("notes", { clerkUserId, ...args });
      return {};
    }
    if (existing.writerId === args.baseWriterId && existing.rev === args.baseRev) {
      await ctx.db.patch(existing._id, args);
      return {};
    }
    const conflictLocalId = `${args.localId}-conflict-${Date.now()}`;
    await ctx.db.insert("notes", {
      clerkUserId,
      ...args,
      localId: conflictLocalId,
      title: `${args.title} (conflict)`,
      pinned: false,
    });
    return { conflictLocalId };
  },
});

export const tombstone = mutation({
  args: { localId: v.string(), deletedAt: v.number() },
  handler: async (ctx, args) => {
    const clerkUserId = await uid(ctx);
    const existing = await ctx.db
      .query("notes")
      .withIndex("by_user_local", (q) => q.eq("clerkUserId", clerkUserId).eq("localId", args.localId))
      .unique();
    if (existing) await ctx.db.patch(existing._id, { deletedAt: args.deletedAt });
  },
});
