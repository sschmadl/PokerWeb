<script setup lang="ts">
const links = [{
  label: 'Home',
  icon: 'i-heroicons-home',
  to: '/'
}, {
  label: 'Settings',
  icon: 'bi:gear',
  to: '/settings'
}]
</script>

<template>
  <UContainer class="w-full flex justify-between items-center border-b border-gray-200 dark:border-gray-800 py-2">
    <!-- Navigation -->
    <div class="flex items-center">
      <UHorizontalNavigation :links="links" />
    </div>

    <div class="flex items-center gap-4">
      <UButton class="rounded-md" to="login" label="Login" variant="ghost" />
      <UButton class="rounded-md" to="register" label="Register" variant="ghost" />

      <NavBarColorModeButton/>
    </div>
  </UContainer>
</template>
