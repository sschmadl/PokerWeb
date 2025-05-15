<script setup lang="ts">
import { ref } from 'vue';

defineProps<{
  tableDiameter: number;
}>();

const sidePotVisible = ref(true);
const potCredits = ref(123456789);   // example value for testing
const sidePotCredits = ref(98765432); // example value for testing
</script>


<template>
  <div
      class="flex flex-nowrap justify-center items-center gap-4"
      :style="{ fontSize: tableDiameter * 0.05 + 'px' }"
  >
    <div
        class="pot-box bg-customPrimary-100 dark:bg-customPrimary-500"
        :style="{
        width: '40%',
        maxWidth: '280px',
        minHeight: `${tableDiameter * 0.07}px`,
        padding: '0.5rem 1rem',
      }"
    >
      <strong class="truncate block w-full text-center">Pot</strong>
      <div class="credits text-black dark:text-white truncate w-full text-center">
        <span>{{ potCredits }}</span>
      </div>
    </div>

    <div
        class="pot-box bg-customPrimary-100 dark:bg-customPrimary-500"
        v-if="sidePotVisible"
        :style="{
        width: '40%',
        maxWidth: '280px',
        minHeight: `${tableDiameter * 0.07}px`,
        padding: '0.5rem 1rem',
      }"
    >
      <strong class="truncate block w-full text-center">Side Pot</strong>
      <div class="credits text-black dark:text-white truncate w-full text-center">
        <span>{{ sidePotCredits }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pot-box {
  @apply inline-flex flex-col items-center justify-center rounded-xl shadow-md;
  box-sizing: border-box;
  text-align: center;
  transition: all 0.3s ease;
}

.credits {
  margin-top: 0.3rem;
  font-family: "Cambria Math", sans-serif;
  white-space: nowrap;
}
</style>
