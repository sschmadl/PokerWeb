<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useUsername } from "~/composables/states";
import { object, string, ref as yupRef, type InferType } from 'yup';
import type { FormSubmitEvent } from '#ui/types';

const schema = object({
  oldPassword: string().required('Old password is required'),
  newPassword: string()
      .min(8, 'New password must be at least 8 characters')
      .required('New password is required'),
  newPasswordConfirm: string()
      .oneOf([yupRef('newPassword')], "New passwords don't match!")
      .required('Please confirm your new password')
});

type Schema = InferType<typeof schema>;

const state = reactive({
  oldPassword: '',
  newPassword: '',
  newPasswordConfirm: ''
});

const errorMessage = ref('');

const submitForm = async (event: FormSubmitEvent<Schema>) => {
  try {
    const changePasswordResponse = await useFetch('/auth/change-password', {
      method: 'POST',
      body: {
        username: useUsername().value,
        oldPassword: event.data.oldPassword,
        newPassword: event.data.newPassword
      }
    });

    if (changePasswordResponse.status.value === 'success') {
      navigateTo('/user-management');
    } else {
      console.error('Error trying to change password');
    }
  } catch (error) {
    console.error('Network or server error:', error);
  }
};

const cancelChange = () => {
  navigateTo('/user-management');
};
</script>

<template>
  <NavBar />
  <div class="flex items-center justify-center pt-20">
    <UCard class="w-full max-w-md p-6">
      <UForm :schema="schema" :state="state" @submit="submitForm" class="space-y-4">
        <UFormGroup name="oldPassword" class="pt-4">
          <UInput v-model="state.oldPassword" type="password" placeholder="Enter old password..." required />
        </UFormGroup>

        <UFormGroup name="newPassword" class="pt-4">
          <UInput v-model="state.newPassword" type="password" placeholder="Enter new password..." required />
        </UFormGroup>

        <UFormGroup name="newPasswordConfirm" class="pt-4">
          <UInput v-model="state.newPasswordConfirm" type="password" placeholder="Confirm new password..." required />
        </UFormGroup>

        <p v-if="errorMessage" class="text-red-500 text-sm pt-2">{{ errorMessage }}</p>

        <div>
          <UButton type="submit" class="mt-6 px-6 py-3 text-lg">Submit</UButton>
          <UButton style="float: right" @click.prevent="cancelChange" class="mt-6 px-6 py-3 text-lg">Cancel</UButton>
        </div>
      </UForm>
    </UCard>
  </div>
</template>