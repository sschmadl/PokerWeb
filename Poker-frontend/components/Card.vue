<script setup>
const props = defineProps({
  faceDown: {
    type: Boolean,
    required: true,
  },
  frontImage: String,
  height: Number,
});

const card_back = '/cards_default/2B.svg';
</script>

<template>
  <div class="card-container" :class="{ faceDown: props.faceDown }" v-if="!faceDown">
    <img class="card-front" :src="frontImage" :style="{ height: props.height + 'px' }"  alt="Playing Card Front"/>
  </div>
  <div class="card-container" v-else>
    <img class="card-front" :src="card_back" :style="{ height: props.height + 'px' }"  alt="Playing Card Front"/>
  </div>
</template>

<style scoped>
  .card-container.faceDown .card-front {
    transform: rotateY(180deg);
  }
</style>
