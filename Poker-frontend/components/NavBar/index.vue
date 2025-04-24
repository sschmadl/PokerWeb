<script setup lang="ts">
import {isLoggedIn, useUsername} from "~/composables/states";
import { useAuth } from "~/composables/useAuth"; // If you have a useAuth composable

const loggedIn = await isLoggedIn();
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
        <UButton
            class="rounded-md"
            to="/login"
            label="Login"
            variant="ghost"
            icon="hugeicons:note-done"
        />
        <UButton
            class="rounded-md"
            to="/register"
            label="Register"
            variant="ghost"
            icon="hugeicons:mortarboard-02"
        />
      </template>

      <template v-else class="flex items-center gap-4">
        <p>{{ useUsername().value }}</p>
        <UButton
            class="rounded-md"
            label="Logout"
            variant="ghost"
            icon="heroicons:arrow-right-on-rectangle"
            @click="useAuth().logout()"
        />
      </template>

      <template v-if="loggedIn">
        <UButton
            class="rounded-md"
            to="user-management"
            label="Account"
            variant="ghost"
            icon="hugeicons:user-edit-01"
        />
      </template>


      <NavBarColorModeButton/>
    </div>
  </UContainer>
</template>
