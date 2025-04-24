<script setup lang="ts">
import { object, string, type InferType } from 'yup';
import type { FormSubmitEvent } from '#ui/types';
import { useAuth } from '~/composables/useAuth';

const schema = object({
  username: string().required('Username is required'),
  password: string().required('Password is required'),
});

type Schema = InferType<typeof schema>;

const state = reactive({
  username: '',
  password: '',
});

const loginError = ref('');

type LoginResponse = {
  token: string;
};

async function submit(event: FormSubmitEvent<Schema>) {
  try {
    const loginResponse = await $fetch<LoginResponse>('/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: {
        username: event.data.username,
        password: event.data.password,
      },
    });

    console.log(loginResponse);

    useAuth().login(event.data.username, loginResponse.token);
    useToast().add({
      title: 'Logged in successfully',
      icon: 'hugeicons:information-circle',
      color: 'green'
    });
  } catch (error: any) {
    if (error.data?.error) {
      useToast().add({
        title: error.data.error,
        icon: 'hugeicons:information-circle',
        color: 'red'
      });
    } else {
      loginError.value = 'An unexpected error occurred';
    }
  }
}

const maxCharLength = 15;
</script>

<template>
  <NavBar />
  <div class="flex items-center justify-center pt-20">
    <UCard class="w-full max-w-md p-6">
      <h2 class="text-2xl font-semibold text-center">Login</h2>
      <UForm :schema="schema" :state="state" @submit="submit" class="space-y-4">
        <UFormGroup name="username" class="pt-4">
          <UInput
              v-model="state.username"
              :maxlength="maxCharLength"
              aria-describedby="character-count"
              placeholder="Username..."
          >
            <template #trailing>
              <div
                  id="character-count"
                  class="text-xs text-gray-500 tabular-nums"
                  aria-live="polite"
                  role="status"
              >
                {{ state.username.length }}/{{ maxCharLength }}
              </div>
            </template>
          </UInput>
        </UFormGroup>

        <UFormGroup name="password" class="pt-4">
          <UInput
              v-model="state.password"
              type="password"
              placeholder="Password..."
          />
        </UFormGroup>

        <UFormGroup name="submit" class="pt-4">
          <UButton type="submit" class="my-4" block>Login</UButton>
        </UFormGroup>

        <div class="w-full text-center flex items-center justify-center">
          <p class="text-red-700 w-full">{{ loginError }}</p>
        </div>
      </UForm>
    </UCard>
  </div>
</template>
