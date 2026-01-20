import {FightList} from "./fight-list/FightList"
import Fight from "./fight/Fight"
import {useEffect, useState} from "react"
import {getFights} from "./shared/api/fight-service"
import {Tabs, TabPanel} from "./tabs/Tabs"

const tabs = [
  {id: "fight", label: "Fight"},
  {id: "history", label: "Fight History"}
]

function App() {
  const [fights, setFights] = useState()
  const [activeTab, setActiveTab] = useState("fight")
  const refreshFights = () => getFights().then(answer => setFights(answer))

  useEffect(() => {
      refreshFights()
    }, []
  )

  return (
    <>
      <h1>
        Welcome to Super Heroes Fight!
      </h1>
      <Tabs tabs={tabs} activeTab={activeTab} onTabChange={setActiveTab}>
        <TabPanel tabId="fight">
          <Fight onFight={refreshFights}/>
        </TabPanel>
        <TabPanel tabId="history">
          <FightList fights={fights}/>
        </TabPanel>
      </Tabs>
    </>
  )
}

export default App
