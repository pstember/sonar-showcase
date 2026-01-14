import axios from 'axios'
import type { Notification, NotificationPreferences } from '../types/notification'

/**
 * Notification service with intentional issues
 * 
 * MNT: 'any' type abuse, console.log spam
 * SEC: Using vulnerable Axios version
 */

// SEC: Hardcoded API URL
const API_URL = 'http://localhost:8080/api/v1'

// MNT: Console spam
console.log('Notification service loaded')
console.log('API URL:', API_URL)

// MNT: Function with 'any' return type
export const fetchNotifications = async (userId?: any, status?: any): Promise<any> => {
  console.log('Fetching notifications:', userId, status)
  // SEC: SQL Injection - passing unsanitized status parameter
  const params: any = {}
  if (userId) params.userId = userId
  if (status) params.status = status  // SEC: Vulnerable to SQL injection
  
  try {
    const response = await axios.get(`${API_URL}/notifications`, { params })
    console.log('Notifications response:', response)
    return response.data
  } catch (error: any) {
    console.log('Error fetching notifications:', error)
    throw error
  }
}

// MNT: 'any' type abuse
export const createNotification = async (notification: any): Promise<any> => {
  console.log('Creating notification:', notification)
  // SEC: XSS - no sanitization of content
  console.log('Notification content:', notification.content)
  
  return axios.post(`${API_URL}/notifications`, notification)
}

// MNT: Function with 'any' parameters
export const getNotificationHistory = async (userId: any, status?: any): Promise<any> => {
  console.log('Fetching notification history:', userId, status)
  // SEC: SQL Injection - passing unsanitized status
  const params: any = { userId }
  if (status) params.status = status
  
  return axios.get(`${API_URL}/notifications/history`, { params })
}

// MNT: 'any' type abuse
export const getNotificationPreferences = async (userId: any): Promise<any> => {
  console.log('Fetching preferences for user:', userId)
  // TODO: Implement proper API call
  return Promise.resolve({})
}

// MNT: Function with 'any' return type
export const updateNotificationPreferences = async (userId: any, preferences: any): Promise<any> => {
  console.log('Updating preferences:', userId, preferences)
  // FIXME: Temporary implementation
  return Promise.resolve(preferences)
}

// MNT: Unused function
export const unusedNotificationFunction = () => {
  console.log('This is never used')
}

export default {
  fetchNotifications,
  createNotification,
  getNotificationHistory,
  getNotificationPreferences,
  updateNotificationPreferences,
}

