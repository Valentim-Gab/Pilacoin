'use client'

import React from 'react'
import FormBtn from '../../../components/form-btn'
import { User } from '@/interfaces/user'
import { Pilacoin } from '@/interfaces/pilacoin'
import { twMerge } from 'tailwind-merge'

export interface TransferProps {
  userList: User[]
  pilacoin: Pilacoin
}

export default function Transfer({ userList, pilacoin }: TransferProps) {
  const [selectedUser, setSelectedUser] = React.useState<User | null>(null)

  function handleSelectUser(user: User) {
    setSelectedUser(user)
  }

  function transfer() {
    if (!selectedUser) return

    console.log(selectedUser)
  }

  return (
    <>
      <ul className="flex flex-row flex-wrap gap-2">
        {userList &&
          userList.length > 0 &&
          userList.map((user) => (
            <li key={user.nome}>
              <button
                className={twMerge(
                  'p-2 border rounded hover:background-gold hover:black-primary',
                  selectedUser?.nome == user.nome ? 'bg-neutral-700' : ''
                )}
                onClick={() => handleSelectUser(user)}
              >
                {user.nome}
              </button>
            </li>
          ))}
      </ul>
      <div className="flex items-center justify-end w-full">
        <FormBtn
          type="button"
          disabled={
            pilacoin?.status.toLowerCase() !== 'valido' || !selectedUser
          }
          onClick={transfer}
        >
          <i className="icon-[solar--round-transfer-horizontal-bold]"></i>
          Transferir
        </FormBtn>
      </div>
    </>
  )
}
