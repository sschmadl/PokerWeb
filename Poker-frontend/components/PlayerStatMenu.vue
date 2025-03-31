<script setup lang="ts">

const props = defineProps({
  menuWidth: {
    type: Number,
    required: true,
  }
});

// Colors

const informationContainerWidth = props.menuWidth * 0.75;
const userNameContainerHeight = props.menuWidth * 0.1;
const userNameContainerFontSize = userNameContainerHeight * 0.5;

const pokerhandContainerHeight = props.menuWidth * 0.12
const pokerhandContainerFontSize = pokerhandContainerHeight * 0.5;

</script>

<template>
  <div class="menu-container" :style="{
    width: menuWidth + 'px',
  }">
    <div class="player-stat-container" :style="{
      width: informationContainerWidth + 'px',
    }">
      <div class="player-username-container bg-gray-400 dark:bg-gray-700" :style="{
        width: informationContainerWidth + 'px',
        height: userNameContainerHeight + 'px',
        fontSize: userNameContainerFontSize + 'px',
      }">
        <span class="username-text">Daniel Rewobourggggggggggggg</span>
      </div>
      <div class="player-pokerhand-money-container bg-gray-500 dark:bg-gray-600" :style="{
        width: informationContainerWidth + 'px',
        height: pokerhandContainerHeight + 'px',
        fontSize: pokerhandContainerFontSize + 'px',
      }">
        <span >Three of a Kind</span>
        <span >187 €</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.player-username-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.username-text {
  display: block;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width: 100%;
}

.player-pokerhand-money-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

</style>