<script setup>
import { computed } from 'vue';

const props = defineProps({
  faceDown: {
    type: Boolean,
    required: true,
  },
  frontImage: String,
  height: {
    type: Number,
    required: true,
  },
  highlighted: Boolean,
});

const card_back = '/cards_default/2B.svg';
const card_front = computed(() => props.frontImage !== undefined ? props.frontImage : '/cards_default/1J.svg');
const highlighted = computed(() => props.highlighted !== undefined ? props.highlighted : false);
</script>

<template>
  <div class="card-container" :class="{ faceDown: props.faceDown }" :style="highlighted ? 'box-shadow: 0 0 40px rgba(255, 255, 255, 0.5);' : ''" v-if="!props.faceDown">
    <img class="card-front" :src="card_front" :style="{ height: props.height + 'px' }" alt="Playing Card Front"/>
  </div>
  <div class="card-container" v-else>
    <img class="card-front" :src="card_back" :style="{ height: props.height + 'px' }" alt="Playing Card Back"/>
  </div>
</template>

<style scoped>
.card-container {
  display: inline-block;
  transition: box-shadow 0.3s ease-in-out;
}
</style>
