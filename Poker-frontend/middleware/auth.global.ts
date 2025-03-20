import { useLoggedIn } from '~/composables/states';

export default defineNuxtRouteMiddleware((to) => {
    const loggedIn = useLoggedIn();

    const publicPages = ['/login', '/', '/register'];

    if (publicPages.includes(to.path)) {
        return;
    }

    if (!loggedIn.value) {
        console.log('Not logged in, redirecting to /login');
        return navigateTo('/login');
    }

    console.log('User is logged in, proceeding to', to.fullPath);
});
