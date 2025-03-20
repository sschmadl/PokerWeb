<script setup lang="ts">
const items = [{
  label: 'Find existing game',
  key: 'find-game',
  icon: '',
  buttonText: 'Join game'
}, {
  label: 'Create new game',
  key: 'create-game',
  icon: '',
  buttonText: 'Create game',
}]

const gameTableRows = [{
    name: 'Xirou\'s game',
    playerCount: '2/6',
    smallBlind: '5€',
    bigBlind: '10€',
  },
  {
    name: 'Günther\'s game',
    playerCount: '4/7',
    smallBlind: '50€',
    bigBlind: '100€',
  },
]

const gameTableColumns = [{
  key: 'name',
  label: 'Name'
}, {
  key: 'playerCount',
  label: 'Players'
}, {
  key: 'smallBlind',
  label: 'Small Blind'
}, {
  key: 'bigBlind',
  label: 'Big Blind'
}, {
  key: 'select',
  class: 'w-2',
},]

const selected = ref([gameTableColumns[1]])

</script>

<template>
  <NavBar/>
  <UContainer class="w-1/2">
    <UTabs :items="items" class="">
      <template #item="{ item }">
        <UCard class="align-items-center text-center">
          <template #header>
            <p class="text-base font-semibold leading-6 text-gray-900 dark:text-white">
              {{ item.label }}
            </p>
            <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
              {{ item.description }}
            </p>
          </template>

          <div v-if="item.key === 'find-game'" class="space-y-3">
            <UTable v-model="selected" :rows="gameTableRows" :columns="gameTableColumns" class="text-left" />
          </div>
          <div v-else-if="item.key === 'create-game'" class="space-y-3">
            <p>Create Game</p>
          </div>

          <template #footer>
            <UButton type="submit">
              {{ item.buttonText }}
            </UButton>
          </template>
        </UCard>
      </template>
    </UTabs>
  </UContainer>
</template>

<style scoped>

</style>