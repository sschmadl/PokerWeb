<script setup lang="ts">
import { ref } from 'vue';

const formData = ref({
  oldPassword: '',
  newPassword: '',
  newPasswordConfirm: ''
});

const errorMessage = ref('');

const submitForm = () => {
  if (formData.value.newPassword !== formData.value.newPasswordConfirm) {
    errorMessage.value = "New passwords don't match!";
    return;
  }

  errorMessage.value = ''; // Clear any previous errors
  console.log('Form submitted:', formData.value);
};
</script>

<template>
  <PokerSuitBackground/>
  <NavBar/>
  <div class="flex items-center justify-center pt-20">
    <UCard class="w-full max-w-md p-6">
      <form @submit.prevent="submitForm">
        <h2>Old Password:</h2>
        <UFormGroup name="oldPassword" class="pt-4">
          <UInput v-model="formData.oldPassword" type="password" placeholder="Enter old password..." required />
        </UFormGroup>

        <h2>New Password:</h2>
        <UFormGroup name="newPassword" class="pt-4">
          <UInput v-model="formData.newPassword" type="password" placeholder="Enter new password..." required />
        </UFormGroup>

        <h2>Confirm New Password:</h2>
        <UFormGroup name="newPasswordConfirm" class="pt-4">
          <UInput v-model="formData.newPasswordConfirm" type="password" placeholder="Enter new password again..." required />
        </UFormGroup>

        <!-- Error message -->
        <p v-if="errorMessage" class="text-red-500 text-sm pt-2">{{ errorMessage }}</p>

        <UButton type="submit" class="mt-4">Submit</UButton>
      </form>
    </UCard>
  </div>
</template>

<style scoped>
</style>
