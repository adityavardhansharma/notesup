import { mutation } from "./_generated/server";

export const purgeMe = mutation({
  args: {},
  handler: async (ctx) => {
    const identity = await ctx.auth.getUserIdentity();
    if (!identity) throw new Error("UNAUTHENTICATED");
    const clerkUserId = identity.subject;
    for (const table of ["notes", "projects", "media", "users"] as const) {
      const rows = await ctx.db
        .query(table)
        .filter((q) => q.eq(q.field("clerkUserId"), clerkUserId))
        .collect();
      for (const row of rows) await ctx.db.delete(row._id);
    }
  },
});
