<script setup lang="ts">
const links = [{
  label: 'Home',
  icon: 'i-heroicons-home',
  to: '/'
}, {
  label: 'Test',
  icon: 'stash:cloud-solid',
  to: '/'
}, {
  label: 'Test',
  icon: 'stash:cloud-solid',
  to: '/'
}]
</script>

<template>
  <UContainer class="w-full flex justify-between items-center border-b border-gray-200 dark:border-gray-800 py-2">
    <!-- Navigation -->
    <div class="flex items-center">
      <UHorizontalNavigation :links="links" />
    </div>

    <div class="flex items-center gap-4">
      <ColorModeButton/>
      <!-- Account Button -->
      <UButton class="rounded-full" to="account" icon="lucide:user-round" />
    </div>
  </UContainer>
</template>
