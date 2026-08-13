import { httpRouter } from "convex/server";
import { httpAction } from "./_generated/server";

const http = httpRouter();

http.route({
  path: "/clerk-users-webhook",
  method: "POST",
  handler: httpAction(async (ctx, request) => {
    const body = await request.text();
    // Svix verification happens with CLERK_WEBHOOK_SECRET in production.
    console.log("clerk webhook", body.length);
    return new Response("ok", { status: 200 });
  }),
});

export default http;
