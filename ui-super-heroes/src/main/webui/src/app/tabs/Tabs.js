import React from "react"

export function Tabs({tabs, activeTab, onTabChange, children}) {
  return (
    <div className="tabs-container">
      <div className="tabs-header" role="tablist">
        {tabs.map(tab => (
          <button
            key={tab.id}
            role="tab"
            aria-selected={activeTab === tab.id}
            aria-controls={`tabpanel-${tab.id}`}
            className={`tab-button ${activeTab === tab.id ? 'tab-active' : ''}`}
            onClick={() => onTabChange(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </div>
      <div className="tabs-content">
        {React.Children.map(children, child => {
          if (React.isValidElement(child) && child.type === TabPanel) {
            return React.cloneElement(child, {
              isActive: child.props.tabId === activeTab
            })
          }
          return child
        })}
      </div>
    </div>
  )
}

export function TabPanel({tabId, isActive, children}) {
  return (
    <div
      role="tabpanel"
      id={`tabpanel-${tabId}`}
      aria-labelledby={`tab-${tabId}`}
      aria-label={tabId === 'fight' ? 'Fight' : 'Fight History'}
      hidden={!isActive}
      className={`tab-panel ${isActive ? 'tab-panel-active' : ''}`}
    >
      {children}
    </div>
  )
}
