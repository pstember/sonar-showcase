/**
 * Notification types - with intentional issues
 * 
 * MNT: 'any' type abuse
 */

// MNT: Using 'any' instead of proper types
export interface Notification {
  id?: any
  userId?: any
  type?: any
  recipient?: any
  subject?: any
  content?: any  // SEC: Could contain XSS
  status?: any
  priority?: any
  channel?: any
  retryCount?: any
  createdAt?: any
  sentAt?: any
  errorMessage?: any
  metadata?: any
}

// MNT: Another 'any' type
export interface NotificationPreferences {
  id?: any
  userId?: any
  emailEnabled?: any
  smsEnabled?: any
  pushEnabled?: any
  orderConfirmation?: any
  orderShipped?: any
  orderDelivered?: any
  promotional?: any
}

// MNT: 'any' type abuse
export interface WebhookConfiguration {
  id?: any
  name?: any
  url?: any  // SEC: SSRF vulnerability
  eventTypes?: any
  active?: any
  retryCount?: any
  timeoutSeconds?: any
}

// MNT: Generic 'any' type
export type NotificationType = any  // Should be: 'email' | 'sms' | 'push'

// MNT: Another generic type
export type NotificationStatus = any  // Should be: 'pending' | 'sent' | 'failed'

// TODO: Define proper types
// FIXME: Replace 'any' with proper types

