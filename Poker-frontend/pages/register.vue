<script setup lang="ts">
import { object, string, type InferType } from 'yup';
import type { FormSubmitEvent } from '#ui/types';
import { useAuth } from "~/composables/useAuth";

const schema = object({
  username: string()
      .required('Username is required')
      .matches(/^[a-zA-Z0-9_-]*$/, 'Username can only contain letters, numbers, dashes, and underscores'),
  password: string().required('Password is required'),
});

type Schema = InferType<typeof schema>;

const state = reactive({
  username: '',
  password: '',
});

const registerError = ref('');

type RegisterResponse = {
  status: string;
  error?: string;
};

type LoginResponse = {
  token: string;
};

async function submit(event: FormSubmitEvent<Schema>) {
  try {
    const registerResponse = await $fetch<RegisterResponse>('/auth/register', {
      method: 'POST',
      body: {
        username: event.data.username,
        password: event.data.password,
      },
    });

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

    navigateTo('/');
  } catch (error: any) {
    console.error('Fetch error:', error);

    if (error?.data?.error) {
      registerError.value = error.data.error;
    } else if (error?.message) {
      registerError.value = error.message;
    } else {
      registerError.value = 'An unexpected error occurred';
    }
  }
}

const maxCharLength = 15;

function validateUsername() {
  state.username = state.username.replace(/[^a-zA-Z0-9_-]/g, '');
}
</script>

<template>
  <NavBar />
  <div class="flex items-center justify-center pt-20">
    <UCard class="w-full max-w-md p-6">
      <h2 class="text-2xl font-semibold text-center">Register</h2>
      <UForm :schema="schema" :state="state" @submit="submit" class="space-y-4">
        <UFormGroup name="username" class="pt-4">
          <UInput
              v-model="state.username"
              :maxlength="maxCharLength"
              aria-describedby="character-count"
              placeholder="Username..."
              @input="validateUsername"
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
          <UButton type="submit" class="my-4" block>Register</UButton>
        </UFormGroup>

        <div class="w-full text-center flex items-center justify-center">
          <p class="text-red-700 w-full">{{ registerError }}</p>
        </div>
      </UForm>
    </UCard>
  </div>
</template>
