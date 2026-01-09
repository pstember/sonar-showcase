import { useEffect, useState } from 'react'
import { getNotificationHistory } from '../services/notificationService'
import type { Notification } from '../types/notification'

/**
 * NotificationHistory component with SQL injection vulnerability
 * 
 * SEC: SQL Injection - passing unsanitized status parameter
 * MNT: 'any' type abuse
 */

// MNT: 'any' type abuse
interface NotificationHistoryProps {
  userId?: any
}

function NotificationHistory({ userId }: NotificationHistoryProps) {
  // MNT: 'any' types
  const [notifications, setNotifications] = useState<any[]>([])
  const [loading, setLoading] = useState<any>(false)
  const [status, setStatus] = useState<any>('')  // SEC: SQL Injection - unsanitized

  // MNT: Console spam
  console.log('NotificationHistory rendering')
  console.log('Notifications:', notifications)
  console.log('Status filter:', status)

  useEffect(() => {
    if (userId) {
      loadHistory()
    }
  }, [userId, status])

  // MNT: Function with 'any' return type
  const loadHistory = async () => {
    setLoading(true)
    try {
      // SEC: SQL Injection - passing unsanitized status parameter
      const data: any = await getNotificationHistory(userId, status)
      setNotifications(data || [])
    } catch (error: any) {
      console.log('Error loading notification history:', error)
    } finally {
      setLoading(false)
    }
  }

  // MNT: Handler with 'any' type
  const handleStatusChange = (newStatus: any) => {
    console.log('Changing status filter:', newStatus)
    setStatus(newStatus)
  }

  return (
    <div className="card">
      <h2>Notification History</h2>
      
      <div className="filters">
        <label>
          Filter by Status:
          <select
            value={status}
            onChange={(e: any) => handleStatusChange(e.target.value)}
          >
            <option value="">All</option>
            <option value="pending">Pending</option>
            <option value="sent">Sent</option>
            <option value="failed">Failed</option>
          </select>
        </label>
        <button onClick={loadHistory}>Refresh</button>
      </div>
      
      {loading ? (
        <p>Loading notification history...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Type</th>
              <th>Recipient</th>
              <th>Subject</th>
              <th>Status</th>
              <th>Created At</th>
              <th>Sent At</th>
            </tr>
          </thead>
          <tbody>
            {notifications.map((notification: any) => (
              <tr key={notification.id}>
                <td>{notification.id}</td>
                <td>{notification.type}</td>
                <td>{notification.recipient}</td>
                <td>{notification.subject}</td>
                <td>{notification.status}</td>
                <td>{notification.createdAt ? new Date(notification.createdAt).toLocaleString() : '-'}</td>
                <td>{notification.sentAt ? new Date(notification.sentAt).toLocaleString() : '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      
      {/* MNT: Debug output */}
      <div style={{ marginTop: '10px', fontSize: '12px', color: '#666' }}>
        <p>Total notifications: {notifications.length}</p>
        <p>Status filter: {status || 'All'}</p>
      </div>
    </div>
  )
}

export default NotificationHistory

