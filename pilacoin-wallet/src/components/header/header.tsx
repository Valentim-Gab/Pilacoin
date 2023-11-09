'use client'

import React from 'react'
import './header.scss'
import Navbar from './navbar'
import { usePathname } from 'next/navigation'

export default function Header() {
  const [active, setActive] = React.useState(false)
  const pathname = usePathname()

  function showItems() {
    setActive(!active)
  }

  return (
    <header className="flex h-12 justify-between items-center flex-shrink-0 top-0 sticky sm:h-14 bg-primary">
      <div className="logo flex justify-center items-center self-stretch gap-1 px-2 sm:px-4 gold">
        <i className="icon-[solar--wallet-money-bold] text-2xl sm:text-3xl"></i>
        <p className="font-medium sm:font-semibold">PilaWallet</p>
      </div>
      <button
        onClick={showItems}
        className="flex justify-center items-center flex-shrink-0 self-stretch w-12 sm:hidden hover:gold"
      >
        {!active ? (
          <i className="burger-icon icon-[solar--hamburger-menu-linear] text-3xl"></i>
        ) : (
          <i className="burger-icon icon-[solar--close-square-linear] text-2xl p-2"></i>
        )}
      </button>
      <Navbar active={active} setActive={setActive} pathname={pathname} />
    </header>
  )
}
