import { mutation, query } from "./_generated/server";
import { v } from "convex/values";

async function uid(ctx: any) {
  const identity = await ctx.auth.getUserIdentity();
  if (!identity) throw new Error("UNAUTHENTICATED");
  return identity.subject as string;
}

export const list = query({
  args: {},
  handler: async (ctx) => {
    const clerkUserId = await uid(ctx);
    return ctx.db.query("projects").withIndex("by_user", (q) => q.eq("clerkUserId", clerkUserId)).collect();
  },
});

export const upsert = mutation({
  args: {
    localId: v.string(),
    name: v.string(),
    hue: v.number(),
    emoji: v.optional(v.string()),
    order: v.number(),
    updatedAt: v.number(),
    deletedAt: v.optional(v.number()),
  },
  handler: async (ctx, args) => {
    const clerkUserId = await uid(ctx);
    const existing = await ctx.db
      .query("projects")
      .withIndex("by_user_local", (q) => q.eq("clerkUserId", clerkUserId).eq("localId", args.localId))
      .unique();
    if (!existing) await ctx.db.insert("projects", { clerkUserId, ...args });
    else await ctx.db.patch(existing._id, args);
  },
});

export const tombstone = mutation({
  args: { localId: v.string() },
  handler: async (ctx, args) => {
    const clerkUserId = await uid(ctx);
    const existing = await ctx.db
      .query("projects")
      .withIndex("by_user_local", (q) => q.eq("clerkUserId", clerkUserId).eq("localId", args.localId))
      .unique();
    if (existing) await ctx.db.patch(existing._id, { deletedAt: Date.now() });
  },
});
