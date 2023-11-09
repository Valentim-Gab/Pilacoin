import React from 'react'
import './navbar.scss'
import Link from 'next/link'

interface NavbarProps {
  active: boolean
  pathname: string
  setActive: React.Dispatch<React.SetStateAction<boolean>>
}

export default function Navbar({ active, setActive, pathname }: NavbarProps) {
  const items = [
    {
      icon: 'icon-[solar--wallet-money-linear]',
      text: 'Carteira',
      url: '/',
    },
    {
      icon: 'icon-[solar--graph-up-linear]',
      text: 'Trade',
      url: '/trade',
    },
    {
      icon: 'icon-[solar--shield-user-linear]',
      text: 'Conta',
      url: '/account',
    },
  ]

  return (
    <nav className={`navbar bg-primary w-full ${active ? 'active' : ''} lg:w-fit lg:bg-transparent`}>
      <ul className="items border-t lg:flex lg:border-none lg:px-4 lg:gap-4">
        {items &&
          items.map((item, index) => (
            <Link
              key={index}
              href={item.url}
              onClick={() => {
                setActive(false)
              }}
            >
              <li className="flex items-center self-stretch p-4 cursor-pointer">
                <div
                  className={`item flex items-center gap-2 ${
                    pathname === item.url ? 'text-gold' : ''
                  }`}
                >
                  <i className={`${item.icon} lg:text-2xl`}></i>
                  <p>{item.text}</p>
                </div>
              </li>
            </Link>
          ))}
      </ul>
    </nav>
  )
}
