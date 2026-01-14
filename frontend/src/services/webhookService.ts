import axios from 'axios'
import type { WebhookConfiguration } from '../types/notification'

/**
 * Webhook service with SSRF vulnerabilities
 * 
 * SEC: SSRF vulnerability - no URL validation
 * MNT: 'any' type abuse
 */

// SEC: Hardcoded API URL
const API_URL = 'http://localhost:8080/api/v1'

// MNT: Console spam
console.log('Webhook service loaded')

// MNT: Function with 'any' return type
export const fetchWebhooks = async (): Promise<any> => {
  console.log('Fetching webhooks...')
  try {
    const response = await axios.get(`${API_URL}/webhooks`)
    console.log('Webhooks response:', response)
    return response.data
  } catch (error: any) {
    console.log('Error fetching webhooks:', error)
    throw error
  }
}

// SEC: SSRF vulnerability - no URL validation
// MNT: 'any' type abuse
export const registerWebhook = async (webhook: any): Promise<any> => {
  console.log('Registering webhook:', webhook)
  // SEC: SSRF - no validation of webhook.url
  console.log('Webhook URL:', webhook.url)  // Could be http://localhost:8080/admin
  
  return axios.post(`${API_URL}/webhooks`, webhook)
}

// SEC: SSRF vulnerability
// MNT: 'any' type abuse
export const testWebhook = async (id: any, url?: any, payload?: any): Promise<any> => {
  console.log('Testing webhook:', id, url, payload)
  // SEC: SSRF - user-controlled URL
  const params: any = {}
  if (url) params.url = url  // SEC: No validation
  if (payload) params.payload = payload
  
  return axios.get(`${API_URL}/webhooks/${id}/test`, { params })
}

// MNT: Function with 'any' parameters
export const deleteWebhook = async (id: any): Promise<any> => {
  console.log('Deleting webhook:', id)
  return axios.delete(`${API_URL}/webhooks/${id}`)
}

export default {
  fetchWebhooks,
  registerWebhook,
  testWebhook,
  deleteWebhook,
}

