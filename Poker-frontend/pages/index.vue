<script setup lang="ts">
import {useJWT, useUsername} from "../composables/states";
</script>

<template>
  <div>
    <PokerSuitBackground/>
    <NavBar/>
    <p>Username: {{ useUsername() }}</p>
    <p>JWT: {{ useJWT() }}</p>
  </div>
</template>

<style scoped>

</style>