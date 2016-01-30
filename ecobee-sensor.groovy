/**
 *  Ecobee Sensor
 *
 *  Copyright 2015 Juan Risso
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Ecobee Sensor", namespace: "smartthings", author: "SmartThings") {
		capability "Sensor"
		capability "Temperature Measurement"
        // Removed motion for non Ecobee3 devices - wve
		//capability "Motion Sensor"
		capability "Refresh"
		capability "Polling"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label:'${currentValue}°', unit:"dF",
				backgroundColors:[
                	// Celsius Color Range
					[value: 0, color: "#1e9cbb"],
					[value: 15, color: "#1e9cbb"],
                    [value: 19, color: "#1e9cbb"],
                    
                    [value: 21, color: "#44b621"],
					[value: 22, color: "#44b621"],
                    [value: 24, color: "#44b621"],
                    
					[value: 21, color: "#d04e00"],
					[value: 35, color: "#d04e00"],
					[value: 37, color: "#d04e00"],
					// Fahrenheit Color Range
                	[value: 40, color: "#1e9cbb"],
					[value: 59, color: "#1e9cbb"],
                    [value: 67, color: "#1e9cbb"],
                    
                    [value: 69, color: "#44b621"], 
					[value: 72, color: "#44b621"],
                    [value: 74, color: "#44b621"],
                    
					[value: 76, color: "#d04e00"],
					[value: 95, color: "#d04e00"],
					[value: 99, color: "#d04e00"]
				]
			)
		}

		/* Removed motion for non Ecobee3 devices - wve
        standardTile("motion", "device.motion") {
			state("active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
			state("inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
		}*/

		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		// Removed motion for non Ecobee3 devices - wve
		main (["temperature"/*,"motion"*/])
		details(["temperature"/*,"motion"*/,"refresh"])
	}
}

def refresh() {
	log.debug "refresh..."
	poll()
}

void poll() {
	log.debug "Executing 'poll' using parent SmartApp"
	parent.pollChildren(this)
}


def generateEvent(Map results) {	
	log.debug "generateEvent(): parsing data $results. F or C? ${getTemperatureScale()}"
	if(results) {
		results.each { name, value ->

			def linkText = getLinkText(device)
			def isChange = false
			def isDisplayed = true
			def event = [name: name, linkText: linkText, handlerName: name]

            
			if (name=="temperature") {
				def sendValue = value// ? convertTemperatureIfNeeded(value.toDouble(), "F", 1): value //API return temperature value in F
				isChange = isTemperatureStateChange(device, name, value.toString())
				isDisplayed = isChange
				event << [value: sendValue, isStateChange: isChange, displayed: isDisplayed]
                // To test at a later date
//				event << [value: sendValue, linkText: linkText, unit: getTemperatureScale(), isStateChange: isChange, displayed: isDisplayed]

			} else {
				isChange = isStateChange(device, name, value.toString())
				isDisplayed = isChange
				event << [value: value.toString(), isStateChange: isChange, displayed: isDisplayed]
			}
			sendEvent(event)
		}
	}
}



//generate custom mobile activity feeds event
def generateActivityFeedsEvent(notificationMessage) {
	sendEvent(name: "notificationMessage", value: "$device.displayName $notificationMessage", descriptionText: "$device.displayName $notificationMessage", displayed: true)
}

