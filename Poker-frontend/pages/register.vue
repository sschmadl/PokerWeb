<script setup lang="ts">
import {object, string, type InferType} from 'yup'
import type {FormSubmitEvent} from '#ui/types'

const schema = object({
  username: string().required('Required'),
  password: string()
      .min(8, 'Must be at least 8 characters')
      .required('Required')
})

type Schema = InferType<typeof schema>

const state = reactive({
  username: undefined,
  password: undefined
})

async function onSubmit(event: FormSubmitEvent<Schema>) {
  console.log(event.data)
}
</script>

<template>
  <NavBar/>
  <UContainer class="flex justify-center items-start min-h-screen pt-24">
    <div class="bg-indigo-300 dark:bg-indigo-800 p-8 rounded-lg shadow-lg max-w-96 w-full">
      <h2 class="text-2xl font-bold text-center mb-4">Register</h2>
      <UForm :schema="schema" :state="state" class="space-y-6 text-lg" @submit="onSubmit">
        <div class=min-h-30>
          <UFormGroup name="username" class="text-xl">
            <UInput placeholder="Username..." v-model="state.username"/>
          </UFormGroup>
        </div>


        <UFormGroup name="password" class="text-xl">
          <UInput placeholder="Password..." v-model="state.password" type="password"/>
        </UFormGroup>

        <div class="flex justify-center">
          <UButton type="submit" class="w-full flex justify-center items-center text-center">
            Submit
          </UButton>
        </div>
      </UForm>
    </div>
  </UContainer>
</template>
