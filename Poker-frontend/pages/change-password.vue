<script setup lang="ts">
import { ref } from 'vue';
import { useUsername } from "~/composables/states";
import { useRouter } from 'vue-router';
import { object, string, type InferType } from 'yup';

const router = useRouter();

const formData = ref({
  oldPassword: '',
  newPassword: '',
  newPasswordConfirm: ''
});

const errorMessage = ref('');

const schema = object({
  username: string().required('Required'),
  password: string()
      .min(8, 'Must be at least 8 characters')
      .required('Required')
});

type Schema = InferType<typeof schema>;

const submitForm = async () => {
  if (formData.value.newPassword !== formData.value.newPasswordConfirm) {
    errorMessage.value = "New passwords don't match!";
    return;
  }

  errorMessage.value = ''; // Clear any previous errors

  try {
    const changePasswordResponse = await useFetch('/auth/change-password', {
      method: 'POST',
      headers: {},
      body: {
        username: useUsername().value,
        newPassword: formData.value.newPassword,
        oldPassword: formData.value.oldPassword,
      }
    });

    if (changePasswordResponse.status.value === 'success') {
      router.push('/user-management');
    } else {
      console.error('Error trying to change password');
    }
  } catch (error) {
    console.error('Network or server error:', error);
  }
};

const cancelChange = () => {
  router.push('/user-management');
};
</script>

<template>
  <NavBar />
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

        <div>
          <UButton type="submit" class="mt-6 px-6 py-3 text-lg">Submit</UButton>
          <UButton @click.prevent="cancelChange" class="mt-6 px-6 py-3 text-lg">Cancel</UButton>
        </div>
      </form>
    </UCard>
  </div>
</template>
