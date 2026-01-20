import React from "react"
import {fireEvent, render, screen} from "@testing-library/react"
import "@testing-library/jest-dom"
import {Tabs, TabPanel} from "./Tabs"
import {act} from "react"

describe("the Tabs component", () => {
  const tabs = [
    {id: "fight", label: "Fight"},
    {id: "history", label: "Fight History"}
  ]

  it("renders tab buttons for each tab", async () => {
    await act(async () => {
      render(
        <Tabs tabs={tabs} activeTab="fight" onTabChange={() => {}}>
          <TabPanel tabId="fight">Fight Content</TabPanel>
          <TabPanel tabId="history">History Content</TabPanel>
        </Tabs>
      )
    })

    expect(screen.getByRole("tab", {name: /^Fight$/i})).toBeInTheDocument()
    expect(screen.getByRole("tab", {name: /^Fight History$/i})).toBeInTheDocument()
  })

  it("marks the active tab as selected", async () => {
    await act(async () => {
      render(
        <Tabs tabs={tabs} activeTab="fight" onTabChange={() => {}}>
          <TabPanel tabId="fight">Fight Content</TabPanel>
          <TabPanel tabId="history">History Content</TabPanel>
        </Tabs>
      )
    })

    const fightTab = screen.getByRole("tab", {name: /^Fight$/i})
    const historyTab = screen.getByRole("tab", {name: /Fight History/i})

    expect(fightTab).toHaveAttribute("aria-selected", "true")
    expect(historyTab).toHaveAttribute("aria-selected", "false")
  })

  it("shows only the active tab panel content", async () => {
    await act(async () => {
      render(
        <Tabs tabs={tabs} activeTab="fight" onTabChange={() => {}}>
          <TabPanel tabId="fight">Fight Content</TabPanel>
          <TabPanel tabId="history">History Content</TabPanel>
        </Tabs>
      )
    })

    expect(screen.getByText("Fight Content")).toBeVisible()
    expect(screen.queryByText("History Content")).not.toBeVisible()
  })

  it("calls onTabChange when a tab is clicked", async () => {
    const onTabChange = jest.fn()

    await act(async () => {
      render(
        <Tabs tabs={tabs} activeTab="fight" onTabChange={onTabChange}>
          <TabPanel tabId="fight">Fight Content</TabPanel>
          <TabPanel tabId="history">History Content</TabPanel>
        </Tabs>
      )
    })

    await act(async () => {
      fireEvent.click(screen.getByRole("tab", {name: /Fight History/i}))
    })

    expect(onTabChange).toHaveBeenCalledWith("history")
  })

  it("shows history tab content when history is active", async () => {
    await act(async () => {
      render(
        <Tabs tabs={tabs} activeTab="history" onTabChange={() => {}}>
          <TabPanel tabId="fight">Fight Content</TabPanel>
          <TabPanel tabId="history">History Content</TabPanel>
        </Tabs>
      )
    })

    expect(screen.queryByText("Fight Content")).not.toBeVisible()
    expect(screen.getByText("History Content")).toBeVisible()
  })

  it("uses proper ARIA attributes for accessibility", async () => {
    await act(async () => {
      render(
        <Tabs tabs={tabs} activeTab="fight" onTabChange={() => {}}>
          <TabPanel tabId="fight">Fight Content</TabPanel>
          <TabPanel tabId="history">History Content</TabPanel>
        </Tabs>
      )
    })

    const tabList = screen.getByRole("tablist")
    expect(tabList).toBeInTheDocument()

    // The active tab panel should be present
    const activePanel = screen.getByRole("tabpanel")
    expect(activePanel).toBeInTheDocument()

    // Both tab buttons should be present
    const tabButtons = screen.getAllByRole("tab")
    expect(tabButtons).toHaveLength(2)
  })
})
