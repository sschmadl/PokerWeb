<script setup lang="ts">

const tableWidth = ref(window.innerWidth / 2.5);
const tableHeight = ref(window.innerHeight / 2);

const cards = ref([
  { frontImage: '/cards_default/AS.svg', faceDown: false, highlighted: false },
  { frontImage: '/cards_default/2S.svg', faceDown: false, highlighted: false },
  { frontImage: '/cards_default/3S.svg', faceDown: false, highlighted: false },
  { frontImage: '/cards_default/4S.svg', faceDown: false, highlighted: false },
  { frontImage: '/cards_default/5S.svg', faceDown: false, highlighted: false },
]);
const cardHeight = ref(tableHeight.value / 3);

function updateSizes() {
  tableWidth.value = window.innerWidth / 2.5;
  tableHeight.value = window.innerHeight / 2;
  cardHeight.value = tableHeight.value / 3;
}

onMounted(() => {
  window.addEventListener('resize', updateSizes);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateSizes);
});

function flipCards() {
  cards.value.forEach((card) => {
    card.faceDown = !card.faceDown;
  })
}

</script>

<template>
  <div class="game-container">
    <div class="poker-table-container">
      <div class="left-edge rounded-full poker-table" :style="{
        width: tableHeight + 'px',
        height: tableHeight + 'px',
        marginRight: tableWidth + 'px'
      }"></div>
      <div class="middle-part poker-table" :style="{
        width: tableWidth + 'px',
        height: tableHeight + 'px',
      }">
      </div>
      <div class="right-edge rounded-full poker-table" :style="{
        width: tableHeight + 'px',
        height: tableHeight + 'px',
        marginLeft: tableWidth + 'px',
      }"></div>
      <div class="community-cards">
        <Card
            v-for="(card, index) in cards"
            :key="index"
            :face-down="card.faceDown"
            :front-image="card.frontImage"
            :height="cardHeight"
            :highlighted="card.highlighted"
        />
      </div>
      <UButton @click="flipCards" :style="{
        zIndex: 15,
      }">Flip</UButton>
    </div>
  </div>
</template>

<style scoped>

.game-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.poker-table-container {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.poker-table {
  background: #35654D;
  position: absolute;
}

.left-edge,
.right-edge {
  border-radius: 50%;
  z-index: -1;
}

.community-cards {
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
  user-select: none;
  pointer-events: none;
}

</style>