import { isLoggedIn } from "~/composables/states";

export default defineNuxtRouteMiddleware(async (to) => {
    const publicPages = ['/login', '/', '/register'];

    if (publicPages.includes(to.path)) {
        return;
    }

    const loggedIn = await isLoggedIn();

    if (!loggedIn) {
        console.log('Not logged in, redirecting to /login');
        return navigateTo('/login');
    }

    console.log('User is logged in, proceeding to', to.fullPath);
});
