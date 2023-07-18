import React from 'react';
import './Header.css';
import { getBackendVersion } from '../../ts/backendIntegration'
import About from './aboutPage/About';

interface HeaderProps {
    frontendVersion: String;
    goLanding: () => void;
}

const Header = ({ frontendVersion, goLanding}: HeaderProps): JSX.Element => {
    const [backendVersion, setBackendVersion] = React.useState("Unavailable")


    React.useEffect(() => {
        getBackendVersion().then((version) => {
            if(version.data == null) return;
            setBackendVersion(version.data)
        })
    }, [])

    return (
        <div className="Header">
            <h1 onClick={() => goLanding()}>TFTFA</h1>
            <About />
            <div className="version">
                <p>Frontend: {frontendVersion}</p>
                <p>Backend: {backendVersion}</p>
            </div>
        </div>
    );
}
export default Header;