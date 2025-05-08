<script setup lang="ts">
import { ref } from 'vue';

const Check = () => {
  console.log("Check");
  showInput.value = false;
};

const Fold = () => {
  console.log("Fold");
  showInput.value = false;
};

const Raise = () => {
  console.log("Raise");
  showInput.value = true; // Show the input field when "Raise" is clicked
};

const MinimizeWindow = () => {
  console.log("MinimizeWindow");
  maximizeWindow.value = false;
};

const MaximizeWindow = () => {
  console.log("MaximizeWindow");
  maximizeWindow.value = true;
};

const ConfirmRaise = () => {
  // Check if the input is a valid number and greater than 0
  if (!raiseAmount.value || raiseAmount.value <= 0 || isNaN(raiseAmount.value)) {
    alert("Please enter a valid raise amount");
    return; // Don't proceed if invalid input
  }

  console.log(`Raise confirmed with amount: ${raiseAmount.value}`);
  // Add any logic for submitting the raise, e.g., sending to an API or updating the game state.

  showInput.value = false; // Hide the input field after confirmation
};

const toggleWindow = () => {
  maximizeWindow.value = !maximizeWindow.value;
  console.log(maximizeWindow.value ? "Maximized" : "Minimized");
};

let showInput = ref(false); // State to show/hide the input field
let maximizeWindow = ref(true);
let pokerhand = ref("Four of a Kind");
let raiseAmount = ref<number | null>(null); // State to hold the raised amount
</script>

<template>
  <div class="min-h-screen flex items-end justify-start px-4">
    <!-- Card -->
    <transition name="slide-fade">
      <UCard v-show="maximizeWindow" class="w-[280px] text-center">
        <h2 class="text-xl">Current Hand: {{pokerhand}}</h2>
        <h2 class="text-xl font-semibold mb-2">Choose an Option</h2>

        <div class="flex flex-col items-center gap-3 mb-2">
          <UButton @click="Check" size="xl" class="centered-button">Check</UButton>
          <UButton @click="Fold" size="xl" class="centered-button">Fold</UButton>
          <UButton @click="Raise" size="xl" class="centered-button">Raise</UButton>
        </div>

        <div v-if="showInput" class="mt-2">
          <input
              v-model.number="raiseAmount"
              type="number"
              min="1"
              step="any"
              placeholder="Enter raise amount"
              class="border p-2 rounded-lg"
          />
        </div>

        <div v-if="showInput" class="mt-2">
          <UButton @click="ConfirmRaise" size="xl" class="centered-button">Confirm Raise</UButton>
        </div>
      </UCard>
    </transition>

    <!-- The toggle button (Always visible, separate from the card) -->
    <div class="fixed top-1/2 left-4 transform -translate-y-1/2 z-10">
      <button @click="toggleWindow" class="bg-black bg-opacity-40 rounded-full p-2 hover:bg-opacity-60 transition-all">
        <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-6 w-6 text-white transition-transform duration-300"
            :class="{ 'rotate-180': !maximizeWindow }"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.centered-button {
  color: white;
  background-color: darkcyan;
  width: 150px;
  justify-content: center;
  align-items: center;
}

.slide-fade-enter-active, .slide-fade-leave-active {
  transition: all 0.4s ease;
}

.slide-fade-enter-from, .slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.fixed {
  position: fixed;
}

.transform {
  transform: translateY(-50%);
}

.z-10 {
  z-index: 10;
}
</style>
