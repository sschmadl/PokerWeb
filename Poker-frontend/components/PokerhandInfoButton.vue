<script setup lang="ts">
import {nextTick, ref, watch} from 'vue'

const cardWidth = ref(50)
const isOpen = ref(false)
const slideoverContent = ref<HTMLElement | null>(null)

const hands = [
  {
    name: 'Royal Flush',
    rank: 1,
    dummyCards: [
      '/cards_default/AS.svg',
      '/cards_default/KS.svg',
      '/cards_default/QS.svg',
      '/cards_default/JS.svg',
      '/cards_default/TS.svg',
    ]
  },
  {
    name: 'Straight Flush',
    rank: 2,
    dummyCards: [
      '/cards_default/AH.svg',
      '/cards_default/2H.svg',
      '/cards_default/3H.svg',
      '/cards_default/4H.svg',
      '/cards_default/5H.svg',
    ],
  },
  {
    name: 'Four of a Kind',
    rank: 3,
    dummyCards: [
      '/cards_default/7S.svg',
      '/cards_default/7H.svg',
      '/cards_default/7C.svg',
      '/cards_default/7D.svg',
    ],
  },
  {
    name: 'Full House',
    rank: 4,
    dummyCards: [
      '/cards_default/4H.svg',
      '/cards_default/4C.svg',
      '/cards_default/4D.svg',
      '/cards_default/KS.svg',
      '/cards_default/KH.svg',
    ]
  },
  {
    name: 'Flush',
    rank: 5,
    dummyCards: [
      '/cards_default/3S.svg',
      '/cards_default/4S.svg',
      '/cards_default/8S.svg',
      '/cards_default/KS.svg',
      '/cards_default/AS.svg',
    ]
  },
  {
    name: 'Straight',
    rank: 6,
    dummyCards: [
      '/cards_default/3H.svg',
      '/cards_default/4S.svg',
      '/cards_default/5C.svg',
      '/cards_default/6D.svg',
      '/cards_default/7D.svg',
    ]
  },
  {
    name: 'Three of a Kind',
    rank: 7,
    dummyCards: [
      '/cards_default/QH.svg',
      '/cards_default/QS.svg',
      '/cards_default/QC.svg',
    ]
  },
  {
    name: 'Two Pair',
    rank: 8,
    dummyCards: [
      '/cards_default/QH.svg',
      '/cards_default/QS.svg',
      '/cards_default/JC.svg',
      '/cards_default/JS.svg',
    ]
  },
  {
    name: 'Pair',
    rank: 9,
    dummyCards: [
      '/cards_default/8H.svg',
      '/cards_default/8S.svg',
    ]
  },
  {
    name: 'High Card',
    rank: 10,
    dummyCards: [
      '/cards_default/AH.svg',
    ]
  },
]

watch(isOpen, async (value) => {
  if (value) {
    await nextTick()
    if (slideoverContent.value) {
      const width = slideoverContent.value.offsetWidth
      cardWidth.value = Math.min(Math.floor((width - 40) / 5), 100) // leave some margin
    }
  }
})
</script>


<template>
  <div>
    <UButton
        class="rounded-full"
        :icon="'hugeicons:cards-02'"
        @click="isOpen = true"
    />

    <USlideover
        v-model="isOpen"
        :ui="{ width: 'max-w-2xl' }"
    >
      <div class="flex flex-col h-full">
        <!-- Content area that will scroll -->
        <div ref="slideoverContent" class="flex-1 overflow-y-auto p-4">
          <div
              v-for="hand in hands"
              :key="hand.name"
              class="flex flex-col items-center mb-6"
          >
            <h1 class="text-center text-lg font-bold mb-2">
              {{ hand.rank }}. {{ hand.name }}
            </h1>
            <div class="flex gap-2 flex-wrap justify-center">
              <img
                  v-for="card in hand.dummyCards"
                  :key="card"
                  :src="card"
                  :style="{ width: cardWidth + 'px' }"
                  alt="card"
              />
            </div>
          </div>
        </div>
      </div>
    </USlideover>
  </div>
</template>


<style scoped>

</style>