<script setup lang="ts">

const tableWidth = ref(window.innerWidth / 2.5);
const tableHeight = ref(window.innerHeight / 2);


function updateSizes() {
  tableWidth.value = window.innerWidth / 2.5;
  tableHeight.value = window.innerHeight / 2;
}

onMounted(() => {
  window.addEventListener('resize', updateSizes);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateSizes);
});

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
      }"></div>
      <div class="right-edge rounded-full poker-table" :style="{
        width: tableHeight + 'px',
        height: tableHeight + 'px',
        marginLeft: tableWidth + 'px',
      }"></div>
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
  z-index: 1;
}

.left-edge,
.right-edge {
  border-radius: 50%;
}

</style>
