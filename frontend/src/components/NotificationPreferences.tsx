import { useEffect, useState } from 'react'
import { getNotificationPreferences, updateNotificationPreferences } from '../services/notificationService'
import type { NotificationPreferences as NotificationPreferencesType } from '../types/notification'

/**
 * NotificationPreferences component with intentional issues
 * 
 * REL: Stale closure in useEffect
 * MNT: 'any' type abuse, console.log spam
 */

// MNT: 'any' type abuse
interface NotificationPreferencesProps {
  userId?: any
}

function NotificationPreferences({ userId }: NotificationPreferencesProps) {
  // MNT: 'any' types everywhere
  const [preferences, setPreferences] = useState<any>({
    emailEnabled: true,
    smsEnabled: false,
    pushEnabled: true,
    orderConfirmation: true,
    orderShipped: true,
    orderDelivered: true,
    promotional: false,
  })
  
  const [loading, setLoading] = useState<any>(false)
  const [saved, setSaved] = useState<any>(false)
  
  // MNT: Console spam
  console.log('NotificationPreferences rendering')
  console.log('Preferences:', preferences)
  console.log('User ID:', userId)

  // REL: Stale closure - missing 'preferences' in dependency array
  useEffect(() => {
    const timer = setTimeout(() => {
      // BUG: Uses stale 'preferences' value
      savePreferences(preferences)
    }, 1000)
    return () => clearTimeout(timer)
  }, []) // Missing 'preferences' dependency

  // MNT: Function with 'any' parameters
  const savePreferences = async (prefs: any) => {
    console.log('Saving preferences:', prefs)
    setLoading(true)
    try {
      await updateNotificationPreferences(userId, prefs)
      setSaved(true)
      setTimeout(() => setSaved(false), 3000)
    } catch (error: any) {
      console.log('Error saving preferences:', error)
    } finally {
      setLoading(false)
    }
  }

  // MNT: Handler with 'any' type
  const handleChange = (field: any, value: any) => {
    console.log('Changing preference:', field, value)
    setPreferences((prev: any) => ({
      ...prev,
      [field]: value,
    }))
  }

  return (
    <div className="card">
      <h2>Notification Preferences</h2>
      
      <div className="preferences-form">
        <label>
          <input
            type="checkbox"
            checked={preferences.emailEnabled || false}
            onChange={(e: any) => handleChange('emailEnabled', e.target.checked)}
          />
          Email Notifications
        </label>
        
        <label>
          <input
            type="checkbox"
            checked={preferences.smsEnabled || false}
            onChange={(e: any) => handleChange('smsEnabled', e.target.checked)}
          />
          SMS Notifications
        </label>
        
        <label>
          <input
            type="checkbox"
            checked={preferences.pushEnabled || false}
            onChange={(e: any) => handleChange('pushEnabled', e.target.checked)}
          />
          Push Notifications
        </label>
        
        <label>
          <input
            type="checkbox"
            checked={preferences.orderConfirmation || false}
            onChange={(e: any) => handleChange('orderConfirmation', e.target.checked)}
          />
          Order Confirmation
        </label>
        
        <label>
          <input
            type="checkbox"
            checked={preferences.orderShipped || false}
            onChange={(e: any) => handleChange('orderShipped', e.target.checked)}
          />
          Order Shipped
        </label>
        
        <label>
          <input
            type="checkbox"
            checked={preferences.orderDelivered || false}
            onChange={(e: any) => handleChange('orderDelivered', e.target.checked)}
          />
          Order Delivered
        </label>
        
        <label>
          <input
            type="checkbox"
            checked={preferences.promotional || false}
            onChange={(e: any) => handleChange('promotional', e.target.checked)}
          />
          Promotional Emails
        </label>
      </div>
      
      <button
        onClick={() => savePreferences(preferences)}
        disabled={loading}
      >
        {loading ? 'Saving...' : 'Save Preferences'}
      </button>
      
      {saved && <p style={{ color: 'green' }}>Preferences saved!</p>}
      
      {/* MNT: Debug output */}
      <div style={{ marginTop: '10px', fontSize: '12px', color: '#666' }}>
        <pre>{JSON.stringify(preferences, null, 2)}</pre>
      </div>
    </div>
  )
}

export default NotificationPreferences

