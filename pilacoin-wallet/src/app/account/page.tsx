import FormUser from '@/components/form-user'
import { UserService } from '@/services/user-service'
import './account.scss'

export default async function Account() {
  const userService = new UserService()
  const user = await userService.getUser()

  return (
    <main className="account flex min-h-screen flex-col items-center flex-1 self-stretch lg:py-8">
      <section className="section-form flex flex-col items-center p-2 gap-2 w-full sm:px-4 lg:border lg:rounded">
        <FormUser user={user} />
      </section>
    </main>
  )
}
