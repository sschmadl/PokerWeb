<script setup lang="ts">
import {useLoggedIn, useUsername} from "~/composables/states";
import { useAuth } from "~/composables/useAuth"; // If you have a useAuth composable

const loggedIn = useLoggedIn();
</script>

<template>
  <UContainer class="w-full flex justify-between items-center">
    <!-- Navigation -->
    <div class="flex items-center">
      <UHorizontalNavigation :links="[{ label: 'Home', icon: 'i-heroicons-home', to: '/' }, { label: 'Settings', icon: 'bi:gear', to: '/settings' }]" />
    </div>

    <!-- Conditional Buttons: Show Login/Register if NOT logged in, otherwise show Logout -->
    <div class="flex items-center gap-4">
      <template v-if="!loggedIn">
        <UButton class="rounded-md" to="/login" label="Login" variant="ghost" />
        <UButton class="rounded-md" to="/register" label="Register" variant="ghost" />
      </template>
      <template v-else class="flex items-center gap-4">
        <p>{{ useUsername().value }}</p>
        <UButton class="rounded-md" label="Logout" variant="ghost" @click="useAuth().logout()" />
      </template>

      <NavBarColorModeButton/>
    </div>
  </UContainer>
</template>
