import { test, expect } from '@playwright/test'

test('Hello World page renders with MUI components', async ({ page }) => {
  await page.goto('/')

  await expect(page.getByRole('heading', { name: 'Hello World' })).toBeVisible()

  await expect(page.getByRole('button', { name: 'Get Started' })).toBeVisible()
})
