<script setup lang="ts">
import { ref } from 'vue';

const Check = () => {
  console.log("Check");
  showInput.value=false;
};

const Fold = () => {
  console.log("Fold");
  showInput.value=false;
};

const Raise = () => {
  console.log("Raise");
  showInput.value = true; // Show the input field when "Raise" is clicked
};

const MinimizeWindow = () => {
  console.log("MinimizeWindow");
  maximizeWindow.value=false;
}

const MaximizeWindow = () => {
  console.log("MaximizeWindow");
  maximizeWindow.value=true;
}

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
let pokerhand = ref("Four of a Kind")
const raiseAmount = ref<number | null>(null); // State to hold the raised amount
</script>

<template>
  <transition name="slide-fade">
    <div v-show="maximizeWindow" class="min-h-screen flex items-end justify-center px-[20vw]">

    <UCard class="w-full text-center">
      <h2 class="text-xl">Current Hand: {{pokerhand}}</h2>
      <h2 class="text-xl font-semibold mb-4">Choose an Option</h2>

      <div class="flex justify-center gap-4 mb-4">
        <UButton @click="Check" size="xl" class="centered-button">Check</UButton>
        <UButton @click="Fold" size="xl" class="centered-button">Fold</UButton>
        <UButton @click="Raise" size="xl" class="centered-button">Raise</UButton>
      </div>

      <!-- Conditional rendering for the raise amount input field -->
      <div v-if="showInput" class="mt-4">
        <input
            v-model="raiseAmount"
            type="number"
            placeholder="Enter raise amount"
            class="border p-2 rounded-lg"
        />
      </div>

      <!-- Confirm button appears only when input field is visible -->
      <div v-if="showInput" class="mt-4">
        <UButton @click="ConfirmRaise" size="xl" class="centered-button">Confirm Raise</UButton>
      </div>
    </UCard>
  </div>
  </transition>
  <!-- Chevron toggle icon (always visible, fixed at bottom center) -->
  <div class="flex justify-center w-full mb-4">
    <button @click="toggleWindow" class="bg-black bg-opacity-40 rounded-full p-2 hover:bg-opacity-60 transition-all">
      <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-6 w-6 text-white transition-transform duration-300"
          :class="{ 'rotate-180': maximizeWindow }"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7" />
      </svg>
    </button>
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
  overflow: hidden;
  max-height: 1000px; /* big enough to show all content */
}

.slide-fade-enter-from, .slide-fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
  max-height: 0;
}

</style>