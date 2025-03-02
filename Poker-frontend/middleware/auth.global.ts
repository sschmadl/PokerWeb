import {useLoggedIn} from "~/composables/states";

export default defineNuxtRouteMiddleware((to, from) => {
    if (useLoggedIn()) {

    }
})