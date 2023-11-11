'use client'

import React, { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import * as z from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from './ui/form'
import { Input } from '@/components/ui/input'
import { Button } from './ui/button'
import './form-user.scss'
import { UserService } from '@/services/user-service'
import { User } from '@/interfaces/user'

interface ValidationMessageInterface {
  fieldname: string
  min: number
  max: number
  email?: boolean
}

const generateValidationMessage = ({
  fieldname,
  min,
  max,
  email,
}: ValidationMessageInterface) => {
  const fieldShape: Record<string, z.ZodString> = {
    [fieldname]: z
      .string()
      .min(min, {
        message: `${fieldname} deve possuir no mínimo ${min} caracteres.`,
      })
      .max(max, {
        message: `${fieldname} deve possuir no máximo ${max} caracteres.`,
      }),
  }

  if (email) {
    fieldShape.email = z.string().email({ message: 'Email inválido.' })
  }

  return fieldShape
}

const formSchema = z.object({
  ...generateValidationMessage({
    fieldname: 'name',
    min: 5,
    max: 20,
  }),
  ...generateValidationMessage({
    fieldname: 'username',
    min: 5,
    max: 50,
  }),
  ...generateValidationMessage({
    fieldname: 'email',
    min: 5,
    max: 200,
    email: true,
  }),
})

export default function FormUser({ user }: { user: User | undefined }) {
  const [readonly, setReadonly] = useState(true)

  const fields = [
    {
      name: 'name' as const,
      label: 'Nome',
      placeholder: 'Escreva o seu nome completo',
    },
    {
      name: 'username' as const,
      label: 'Username',
      placeholder: 'Escreva o nome de usuário',
    },
    {
      name: 'email' as const,
      label: 'Email',
      placeholder: 'Escreva seu endereço de email',
    },
  ]

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: user?.username ?? '',
      name: user?.name ?? '',
      email: user?.email ?? '',
    },
  })

  function onCancel() {
    form.reset()

    setReadonly(true)
  }

  function onSubmit(values: z.infer<typeof formSchema>) {
    console.info(values)

    setReadonly(true)
  }

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className="space-y-4 w-full p-2"
      >
        {fields &&
          fields.map((fieldItem, index) => (
            <FormField
              control={form.control}
              name={fieldItem.name}
              key={index}
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-xs font-light 2xl:text-sm">
                    {fieldItem.label}:
                  </FormLabel>
                  <FormControl>
                    <Input
                      placeholder={fieldItem.placeholder}
                      className="form-input text-xs read-only:border-none read-only:p-0 read-only:h-fit sm:text-sm sm:p-4 sm:h-fit 2xl:text-base"
                      {...field}
                      readOnly={readonly}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          ))}
        <div className="flex flex-col items-end">
          {readonly ? (
            <Button
              type="button"
              onClick={() => {
                setReadonly(false)
              }}
              className="flex py-1 px-2 justify-center items-center gap-1 h-fit bg-gold sm:px-3 sm:text-base sm:font-bold 2xl:text-lg 2xl:py-2 2xl:px-4"
            >
              <i className="icon-[solar--pen-2-bold] text-base sm:text-2xl"></i>
              Editar
            </Button>
          ) : (
            <div className="flex gap-2">
              <Button
                type="button"
                onClick={onCancel}
                className="flex py-1 px-2 justify-center items-center gap-1 h-fit bg-gray-400 sm:px-3 sm:text-base sm:font-bold 2xl:text-lg 2xl:py-2 2xl:px-4"
              >
                <i className="icon-[solar--close-circle-outline] text-base sm:text-2xl"></i>
                Cancelar
              </Button>
              <Button
                type="submit"
                className="flex py-1 px-2 justify-center items-center gap-1 h-fit bg-gold sm:px-3 sm:text-base sm:font-bold 2xl:text-lg 2xl:py-2 2xl:px-4"
              >
                <i className="icon-[solar--verified-check-linear] text-base sm:text-2xl"></i>
                Enviar
              </Button>
            </div>
          )}
        </div>
      </form>
    </Form>
  )
}
