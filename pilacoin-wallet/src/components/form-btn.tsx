import React, { ButtonHTMLAttributes, ReactNode } from 'react'
import { Button } from './ui/button'
import { tv, VariantProps } from 'tailwind-variants'

const button = tv({
  base: 'flex py-1 px-2 justify-center items-center gap-1 h-fit bg-gold sm:px-3 sm:text-base sm:font-bold 2xl:text-lg 2xl:py-2 2xl:px-4',
  variants: {
    color: {
      base: 'bg-gold',
      cancel: 'bg-gray-400',
    },
  },
  defaultVariants: {
    color: 'base',
  },
})

interface FormBtnProps extends VariantProps<typeof button> {
  type: 'button' | 'submit' | 'reset' | undefined
  children: ReactNode
  onClick?: () => void
}

export default function FormBtn({
  type,
  onClick,
  children,
  color,
}: FormBtnProps) {
  return (
    <Button type={type} className={button({ color })} onClick={onClick}>
      {children}
    </Button>
  )
}
